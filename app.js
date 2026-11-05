const { createApp, ref, computed, onMounted, onUnmounted } = Vue;

createApp({
  setup() {
    const estado = ref(null);
    const carregando = ref(false);
    const jogando = ref(false);
    let intervalo = null;

    const botaoClasse = 'px-4 py-2 text-[11px] sm:text-xs tracking-widest rounded-md border border-cyan-400/40 text-cyan-200 bg-cyan-500/5 hover:bg-cyan-500/15 hover:shadow-[0_0_14px_rgba(34,211,238,0.35)] transition disabled:opacity-30 disabled:cursor-not-allowed disabled:hover:bg-cyan-500/5';

    async function buscarEstado() {
      const resposta = await fetch('/estado');
      estado.value = await resposta.json();
    }

    async function chamar(url) {
      carregando.value = true;
      try {
        const resposta = await fetch(url, { method: 'POST' });
        estado.value = await resposta.json();
      } finally {
        carregando.value = false;
      }
    }

    function proximaRodada() {
      chamar('/rodada');
    }

    function pararPlay() {
      jogando.value = false;
      if (intervalo) {
        clearInterval(intervalo);
        intervalo = null;
      }
    }

    function alternarPlay() {
      if (jogando.value) {
        pararPlay();
        return;
      }
      jogando.value = true;
      intervalo = setInterval(() => {
        if (estado.value && estado.value.terminou) {
          pararPlay();
          return;
        }
        proximaRodada();
      }, 1000);
    }

    function reiniciar() {
      pararPlay();
      chamar('/reiniciar');
    }

    function alternarPosto(numero) {
      chamar('/postos/' + numero + '/alternar');
    }

    function tipoSenha(codigo) {
      if (!codigo) return null;
      return codigo.charAt(0) === 'P' ? 'prioritaria' : 'normal';
    }

    function estadoPosto(posto) {
      if (!posto.ativo) return 'inativo';
      return posto.ocupado ? 'ocupado' : 'livre';
    }

    function corLinhaLog(linha) {
      const marca = linha.trim().charAt(0);
      if (marca === '+') return 'text-cyan-300';
      if (marca === '-') return 'text-orange-400';
      if (marca === '>') return 'text-amber-300';
      if (marca === '*') return 'text-fuchsia-300';
      return 'text-cyan-100/70';
    }

    onMounted(buscarEstado);
    onUnmounted(pararPlay);

    const fila = computed(() => estado.value.fila);
    const proximas = computed(() => estado.value.proximasSenhas);
    const postos = computed(() => estado.value.postos);
    const terminou = computed(() => estado.value.terminou);
    const posto4 = computed(() => postos.value.find(p => p.numero === 4));
    const posto5 = computed(() => postos.value.find(p => p.numero === 5));

    return {
      estado, carregando, jogando, botaoClasse,
      fila, proximas, postos, terminou, posto4, posto5,
      proximaRodada, alternarPlay, reiniciar, alternarPosto,
      tipoSenha, estadoPosto, corLinhaLog
    };
  }
}).mount('#app');

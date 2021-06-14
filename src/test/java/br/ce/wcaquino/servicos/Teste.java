package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Locacao;
import buildermaster.BuilderMaster;

public class Teste {

	public static void main(String[] args) {
		BuilderMaster bm = new BuilderMaster();
		bm.gerarCodigoClasse(Locacao.class);
	}

}

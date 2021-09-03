package com.premiumminds.saftsender.api;

import java.nio.file.Path;
import java.util.Optional;

public class Arguments {

	public enum Version {
		R02("Portaria n.º 160/2013"),
		R03("Portaria n.º 274/2013"),
		R04("Portaria n.º 302/2016");

		private final String description;

		Version(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
	}

	public enum Operation{
		VALIDATE,
		SEND
	}

	private Path input;
	private Optional<Path> output;
	private Optional<Path> resumido;
	private String nif;
	private String password;
	private String ano;
	private Integer mes;
	private Operation op;
	private boolean testes;
	private Optional<Version> version;

	private Arguments(
		final Path input,
		final Optional<Path> output,
		final Optional<Path> resumido,
		final String nif,
		final String password,
		final String ano,
		final Integer mes,
		final Operation op,
		final boolean testes,
		final Optional<Version> version)
	{
		this.input = input;
		this.output = output;
		this.resumido = resumido;
		this.nif = nif;
		this.password = password;
		this.ano = ano;
		this.mes = mes;
		this.op = op;
		this.testes = testes;
		this.version = version;
	}

	public Path getInput() {
		return input;
	}

	public Optional<Path> getOutput() {
		return output;
	}

	public Optional<Path> getResumido() {
		return resumido;
	}

	public String getNif() {
		return nif;
	}

	public String getPassword() {
		return password;
	}

	public String getAno() {
		return ano;
	}

	public Integer getMes() {
		return mes;
	}

	public Operation getOp() {
		return op;
	}

	public boolean isTestes() {
		return testes;
	}

	public Optional<Version> getVersion() {
		return version;
	}

	@Override
	public String toString() {
		return "Arguments{" +
			"input='" + input + '\'' +
			", output=" + output +
			", resumido=" + resumido +
			", nif='" + nif + '\'' +
			", password=*****" +
			", ano='" + ano + '\'' +
			", mes=" + mes +
			", op='" + op + '\'' +
			", testes=" + testes +
			", version=" + version +
			'}';
	}

	public static class Builder {

		private Path input;
		private Path output;
		private Path resumido;
		private String nif;
		private String password;
		private String ano;
		private Integer mes;
		private Operation op;
		private boolean testes;
		private Version version;

		private Builder() {
		}

		public static Builder builder(){
			return new Builder();
		}

		public Builder withInput(final Path input) {
			this.input = input;
			return this;
		}

		public Builder withOutput(final Path output) {
			this.output = output;
			return this;
		}

		public Builder withResumido(final Path resumido) {
			this.resumido = resumido;
			return this;
		}

		public Builder withNif(final String nif) {
			this.nif = nif;
			return this;
		}

		public Builder withPassword(final String password) {
			this.password = password;
			return this;
		}

		public Builder withAno(final String ano) {
			this.ano = ano;
			return this;
		}

		public Builder withMes(final Integer mes) {
			this.mes = mes;
			return this;
		}

		public Builder withOp(final Operation op) {
			this.op = op;
			return this;
		}

		public Builder withTestes(final boolean testes) {
			this.testes = testes;
			return this;
		}

		public Builder withVersion(final Version version) {
			this.version = version;
			return this;
		}

		public Arguments build() {
			return new Arguments(input,
								 Optional.ofNullable(output),
								 Optional.ofNullable(resumido),
								 nif,
								 password,
								 ano,
								 mes,
								 op,
								 testes,
								 Optional.ofNullable(version));
		}
	}
}

package com.premiumminds.saftsender.sender;

import com.premiumminds.saftsender.api.Arguments;
import com.premiumminds.saftsender.api.ISender;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import pt.at.factemicli.cmdProcessor.FactemicliCmdClient;

public class FACTEMICLISender implements ISender {

	@Override
	public void send(final Arguments arguments, OutputStream stream) {
		captureStdOut(() -> FactemicliCmdClient.main(convertToArgs(arguments)), stream);
	}

	private void captureStdOut(Runnable runnable, OutputStream stream){
		System.setOut(new PrintStream(stream, true));
		runnable.run();
		System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
	}

	private String[] convertToArgs(final Arguments arguments) {
		var args = new ArrayList<String>();
		args.add("--input");
		args.add(arguments.getInput().toAbsolutePath().toString());
		arguments.getOutput().ifPresent(v -> {
			args.add("--output");
			args.add(v.toAbsolutePath().toString());
		});
		arguments.getResumido().ifPresent(v -> {
			args.add("--resumido");
			args.add(v.toAbsolutePath().toString());
		});
		args.add("--nif");
		args.add(arguments.getNif());
		args.add("--password");
		args.add(arguments.getPassword());
		args.add("--ano");
		args.add(arguments.getAno());
		args.add("--mes");
		args.add(String.format("%02d", arguments.getMes()));
		args.add("--operacao");
		args.add(switch (arguments.getOp()){
			case SEND -> "enviar";
			case VALIDATE -> "validar";
		});
		if (arguments.isTestes()) {
			args.add("--testes");
		}
		arguments.getVersion().ifPresent(v -> {
			args.add("--version");
			args.add(switch (v) {
				case R02 -> "R02";
				case R03 -> "R03";
				case R04 -> "R04";
			});
		});
		return args.toArray(new String[0]);
	}
}

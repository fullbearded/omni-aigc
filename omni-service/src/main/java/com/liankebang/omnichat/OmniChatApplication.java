package com.liankebang.omnichat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: Runner.dada
 * @date: 2020/12/15
 * @description: The enter gateway for Spring boot
 **/
@SpringBootApplication
public class OmniChatApplication {

	public static void main(String[] args) {
		SpringApplication.run(OmniChatApplication.class, args);
		System.out.println("Omni Chat 启动成功..."
			+ "\n"
			+ "   ____                  _    _____ _           _   \n"
			+ "  / __ \\                (_)  / ____| |         | |  \n"
			+ " | |  | |_ __ ___  _ __  _  | |    | |__   __ _| |_ \n"
			+ " | |  | | '_ ` _ \\| '_ \\| | | |    | '_ \\ / _` | __|\n"
			+ " | |__| | | | | | | | | | | | |____| | | | (_| | |_ \n"
			+ "  \\____/|_| |_| |_|_| |_|_|  \\_____|_| |_|\\__,_|\\__|\n"
			+ "                                                    \n"
			+ "");
	}
}

package br.ufrn.imd.bioinfo.projetos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        // TODO Auto-generated method stub

    }

}

package com.petsup.api.util;

import com.petsup.api.entities.Agendamento;
import com.petsup.api.entities.Pet;
import com.petsup.api.entities.enums.Especie;
import com.petsup.api.entities.enums.NomeServico;
import com.petsup.api.entities.enums.Raca;
import com.petsup.api.service.dto.AgendamentoDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;

public class GeradorTxt {

    //Adiciona linha ao arquivo sem criar um arquivo novo
    public static ResponseEntity<Void> gravaRegistro(String registro, String nomeArq) {
        BufferedWriter saida = null;

        // try-catch para abrir o arquivo
        try {
            saida = new BufferedWriter(new FileWriter(nomeArq, true));
        }
        catch (IOException erro) {
            System.out.println("Erro ao abrir o arquivo");
            ResponseEntity.status(400).build();
        }

        // try-catch para gravar o registro e finalizar
        try {
            saida.append(registro + "\n");
            saida.close();
        }
        catch (IOException erro) {
            System.out.println("Erro ao gravar no arquivo");
            return ResponseEntity.status(400).build();
        }
        return ResponseEntity.status(200).build();
    }

    public static ResponseEntity<Void> gravaArquivoTxt(ListaObj<Agendamento> lista) {
        int contaRegistroDado = 0;
        String nomeArq = "Agendamento.txt";
        Path diretorioBase;

        if(System.getProperty("os.name").contains("Windows")){
            diretorioBase = Path.of(System.getProperty("java.io.tmpdir") + "/arquivos");
        }else{
            diretorioBase = Path.of(System.getProperty("user.dir") + "/arquivos");
        }

        if(!diretorioBase.toFile().exists()){
            diretorioBase.toFile().mkdir();
        }

        // Monta o registro de header
        String header = "00AGENDAMENTO";
        header += LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        header += "01";

        // Grava o registro de header
        gravaRegistro(header, nomeArq);

        // Monta e grava os registros de dados ou registros de corpo
        String corpo;
        for (int i = 0; i < lista.getTamanho(); i++) {
            corpo = "02";
            corpo += String.format("%05d",lista.getElemento(i).getId());
            corpo += String.format("%-19.19s",lista.getElemento(i).getDataHora());
            corpo += String.format("%-50.50s",lista.getElemento(i).getFkCliente().getNome());
            corpo += String.format("%-50.50s",lista.getElemento(i).getFkCliente().getEmail());
            corpo += String.format("%-50.50s",lista.getElemento(i).getFkPet().getNome());
            corpo += String.format("%-8.8s",lista.getElemento(i).getFkPet().getEspecie());
            corpo += String.format("%-1.1s",lista.getElemento(i).getFkPet().getSexo());
            corpo += String.format("%-20.20s",lista.getElemento(i).getFkServico().getNome());
            corpo += String.format("%08.2f",lista.getElemento(i).getFkServico().getPreco());
            gravaRegistro(corpo, nomeArq);
            contaRegistroDado++;
        }

        // Monta e grava o registro de trailer
        String trailer = "01";
        trailer += String.format("%010d",contaRegistroDado);
        gravaRegistro(trailer, nomeArq);
        return ResponseEntity.status(200).build();
    }

    public static ResponseEntity<byte[]> buscaArquivoTxt() {
        File arquivoBuscado;

        if(System.getProperty("os.name").contains("Windows")){
            arquivoBuscado = Path.of(System.getProperty("java.io.tmpdir") + "/arquivos/Agendamento.txt").toFile();
        }else{
            arquivoBuscado = Path.of(System.getProperty("user.dir") + "/arquivos/Agendamento.txt").toFile();
        }

        if(!arquivoBuscado.exists()){
            return ResponseEntity.status(404).build();
        }

        try {
            InputStream fileInputStream = new FileInputStream(arquivoBuscado);

            return ResponseEntity.status(200)
                    .header("Content-Disposition",
                            "attachment; filename=" + arquivoBuscado.getName())
                    .body(fileInputStream.readAllBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new ResponseStatusException(422, "Diretório não encontrado", null);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResponseStatusException(422, "Não foi possível converter para byte[]", null);
        }

    }

    public static ResponseEntity<List<Pet>> leArquivoTxt(@RequestParam("file") MultipartFile arquivo) {
        BufferedReader entrada = null;
        String registro, tipoRegistro;
        String nome, especie, sexo;
        int contaRegDadoLido = 0;
        int qtdRegDadoGravado;


        List<Pet> listaLida = new ArrayList<>();
        // try-catch para abrir o arquivo
        try {
            InputStream inputStream = arquivo.getInputStream();
            entrada = new BufferedReader(new InputStreamReader(inputStream));
        } catch (IOException erro) {
            System.out.println("Erro na abertura do arquivo");
            return ResponseEntity.status(400).build();
        }

        // try-catch para leitura do arquivo
        try {
            registro = entrada.readLine(); // le o primeiro registro do arquivo

            while (registro != null) {
                tipoRegistro = registro.substring(0, 2);     // obtem os 2 primeiros caracteres do registro
                // substring - primeiro argumento é onde começa a substring dentro da string
                // e o segundo argumento é onde termina a substring + 1
                // Verifica se o tipoRegistro é um header, ou um trailer, ou um registro de dados
                if (tipoRegistro.equals("00")) {
                    System.out.println("é um registro de header");
                    System.out.println("Tipo de arquivo: " + registro.substring(2, 5));
                }
                else if (tipoRegistro.equals("01")) {

                    System.out.println("é um registro de trailer");
                    qtdRegDadoGravado = Integer.parseInt(registro.substring(2, 7));
                    if (contaRegDadoLido != qtdRegDadoGravado) {
                        System.out.println("Quantidade de registros lidos não bate com a quantidade declarada");
                        System.out.println(qtdRegDadoGravado);
                        System.out.println(contaRegDadoLido);
                        return ResponseEntity.status(400).build();
                    }
                } else if (tipoRegistro.equals("02")) {
                    System.out.println("é um registro de dados");
                    nome = registro.substring(2, 26).trim();
                    especie = registro.substring(27, 35).trim().toUpperCase();
                    sexo = registro.substring(36,36).trim();

                    Pet p = new Pet();

                    p.setEspecie(Especie.valueOf(especie));
                    p.setNome(nome);
                    p.setSexo(sexo);

                    listaLida.add(p);

                    // contabiliza que leu mais um registro de dados
                    contaRegDadoLido++;
                } else {
                    System.out.println("tipo de registro inválido");
                }
                // le o proximo registro do arquivo
                registro = entrada.readLine();
            }
            entrada.close();
        } catch (IOException erro) {
            System.out.println("Erro ao ler o arquivo");
            return ResponseEntity.status(400).build();
        }
      
        // Para importar a lista toda para o banco de dados:
        // repository.saveAll(listaLida);
        System.out.println("Leu o arquivo OK");
        System.out.println(listaLida);
        return ResponseEntity.status(200).body(listaLida);
    }
}

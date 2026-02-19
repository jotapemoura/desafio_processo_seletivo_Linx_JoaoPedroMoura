package com.desafio.linx.api.service;
import com.desafio.linx.api.model.Venda;
import com.desafio.linx.api.repository.VendaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class VendaService {

    private final VendaRepository repository;

    public VendaService(VendaRepository repository) {
        this.repository = repository;
    }

    
    public void processarArquivo(MultipartFile arquivo) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(arquivo.getInputStream()))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.isEmpty() && linha.startsWith("1")) {
                    Venda venda = parseLinha(linha);
                    repository.save(venda);
                }
            }
        }
    }

    private Venda parseLinha(String linha) {
        Venda venda = new Venda();
        venda.setEstabelecimento(read(linha, 2, 10));
        
        String dataStr = read(linha, 20, 8);
        venda.setDataEvento(LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("yyyyMMdd")));
        
        String horaStr = read(linha, 28, 6);
        venda.setHoraEvento(LocalTime.parse(horaStr, DateTimeFormatter.ofPattern("HHmmss")));
        
        venda.setValorTotal(parseValor(read(linha, 98, 13)));
        venda.setValorLiquido(parseValor(read(linha, 243, 13)));
        venda.setBandeira(read(linha, 262, 30));
        venda.setNsu(read(linha, 330, 11));

        return venda;
    }

    private BigDecimal parseValor(String valorStr) {
        if (valorStr == null || valorStr.isBlank()) return BigDecimal.ZERO;
        try {
            return new BigDecimal(valorStr).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private String read(String linha, int inicio, int tamanho) {
        int start = inicio - 1;
        int end = start + tamanho;
        if (linha.length() < end) return ""; 
        return linha.substring(start, end).trim();
    }

    public List<Venda> buscarDatas(LocalDate inicio, LocalDate fim) {
        if (inicio != null && fim != null) {
            return repository.findByDataEventoBetween(inicio, fim);
        }
        return repository.findAll();
    }

    public List<Venda> buscarSaldos(String ordem) {
        if ("menor".equals(ordem)) {
            return repository.findAll(Sort.by(Sort.Direction.ASC, "valorTotal"));
        } 
        else if ("maior".equals(ordem)) {
            return repository.findAll(Sort.by(Sort.Direction.DESC, "valorTotal"));
        } 
        else {
            return repository.findAll();
        }
    }
}
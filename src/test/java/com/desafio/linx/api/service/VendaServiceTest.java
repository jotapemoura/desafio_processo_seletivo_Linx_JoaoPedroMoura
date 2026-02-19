package com.desafio.linx.api.service; 
import com.desafio.linx.api.model.Venda;
import com.desafio.linx.api.repository.VendaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import java.math.BigDecimal;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendaServiceTest {
    @InjectMocks
    private VendaService vendaService;

    @Mock
    private VendaRepository vendaRepository;

    @Test
    void deveProcessarLinhaDetalheCorretamente() throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 500; i++) sb.append(" ");

        sb.replace(0, 1, "1");
        sb.replace(1, 11, "LOJA TESTE");
        sb.replace(19, 27, "20231025");
        sb.replace(27, 33, "103000");
        sb.replace(97, 110, "0000000015050");
        sb.replace(242, 255, "0000000014000");
        sb.replace(261, 271, "MASTERCARD");
        sb.replace(329, 340, "12345678901");

        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo", "teste.txt", "text/plain", sb.toString().getBytes()
        );

        vendaService.processarArquivo(arquivo);

        ArgumentCaptor<Venda> captor = ArgumentCaptor.forClass(Venda.class);
        verify(vendaRepository, times(1)).save(captor.capture());

        Venda vendaSalva = captor.getValue();

        Assertions.assertEquals("LOJA TESTE", vendaSalva.getEstabelecimento().trim());
        Assertions.assertEquals(new BigDecimal("150.50"), vendaSalva.getValorTotal());
    }
    
    @Test
    void deveIgnorarHeaderETrailer() throws Exception {
        String conteudo = "0HEADER...\n9TRAILER...";
        MockMultipartFile arquivo = new MockMultipartFile("arquivo", conteudo.getBytes());
        vendaService.processarArquivo(arquivo);
        verify(vendaRepository, never()).save(any());
    }
}
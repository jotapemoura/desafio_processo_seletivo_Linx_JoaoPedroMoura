package com.desafio.linx.api.controller;
import com.desafio.linx.api.service.VendaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc 
class VendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VendaService vendaService; 

    @Test
    void deveCarregarPaginaInicial() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk()) 
                .andExpect(view().name("index")) 
                .andExpect(model().attributeExists("vendas")); 
    }

    @Test
    void deveFazerUploadERedirecionar() throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo", "teste.txt", "text/plain", "conteudo".getBytes()
        );

        doNothing().when(vendaService).processarArquivo(any());

        mockMvc.perform(multipart("/upload").file(arquivo))
                .andExpect(status().is3xxRedirection()) 
                .andExpect(redirectedUrl("/")) 
                .andExpect(flash().attributeExists("mensagem")); 
    }
}
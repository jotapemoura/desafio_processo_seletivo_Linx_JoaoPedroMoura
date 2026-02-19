package com.desafio.linx.api.controller;
import com.desafio.linx.api.model.Venda;
import com.desafio.linx.api.service.VendaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.List;

@Controller
public class VendaController {

    private final VendaService service;

    public VendaController(VendaService service) {
        this.service = service;
    }
    
    @GetMapping("/")
    public String index(Model model, 
                        @RequestParam(required = false) LocalDate inicio,
                        @RequestParam(required = false) LocalDate fim,
                        @RequestParam(required = false) String sort) {
        
        List<Venda> vendas;

        if (inicio != null && fim != null) {
            vendas = service.buscarDatas(inicio, fim);
        } 
        else if (sort != null) {
            vendas = service.buscarSaldos(sort);
        } 
        else {
            vendas = service.buscarSaldos(null);
        }
        
        model.addAttribute("vendas", vendas);
        return "index";
    }
    
    @PostMapping("/upload")
    public String upload(@RequestParam("arquivo") MultipartFile arquivo, RedirectAttributes redirectAttributes) {
        try {
            service.processarArquivo(arquivo);
            redirectAttributes.addFlashAttribute("mensagem", "Arquivo processado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao processar: " + e.getMessage());
        }
        return "redirect:/";
    }
}
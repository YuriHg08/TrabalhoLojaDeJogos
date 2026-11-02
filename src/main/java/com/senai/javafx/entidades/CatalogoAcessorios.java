package com.senai.javafx.entidades;

import com.senai.javafx.entidades.Acessorio.Tipo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URL;
import java.util.List;

// Catálogo da Loja de Acessórios: consoles (PS2 ao PS5, Xbox 360 ao Series, Switch 1 e 2),
// PCs gamer e os controles de cada plataforma.
//
// Para trocar uma imagem, substitua o arquivo em
// resources/com/senai/javafx/imagens/acessorios/ (mesmo nome) ou mude o nome aqui embaixo.
public final class CatalogoAcessorios {

    public static final List<String> PLATAFORMAS = List.of("Playstation", "Xbox", "Nintendo", "PC");

    // Lista observável: o administrador cadastra/edita e as telas se atualizam
    public static final ObservableList<Acessorio> TODOS = FXCollections.observableArrayList(
            // ---- PLAYSTATION — CONSOLES ----
            novo("PlayStation 2 Slim", Tipo.CONSOLE, "Playstation", "Slim • edição retrô", 899.90, "ps2_slim.png", 4),
            novo("PlayStation 3 Super Slim", Tipo.CONSOLE, "Playstation", "500 GB", 1499.90, "ps3_super_slim.png", 6),
            novo("PlayStation 4 Slim", Tipo.CONSOLE, "Playstation", "1 TB", 2299.90, "ps4_slim.png", 8),
            novo("PlayStation 4 Pro", Tipo.CONSOLE, "Playstation", "1 TB • 4K", 3199.90, "ps4_pro.png", 6),
            novo("PlayStation 5 Standard", Tipo.CONSOLE, "Playstation", "825 GB • com leitor de disco", 3799.90, "ps5_standard.png", 7),
            novo("PlayStation 5 Digital", Tipo.CONSOLE, "Playstation", "825 GB • sem leitor de disco", 3399.90, "ps5_digital.png", 7),
            novo("PlayStation 5 Slim", Tipo.CONSOLE, "Playstation", "1 TB • com leitor de disco", 4299.90, "ps5_slim.png", 10),
            novo("PlayStation 5 Slim Digital", Tipo.CONSOLE, "Playstation", "1 TB • sem leitor de disco", 3799.90, "ps5_slim_digital.png", 10),
            novo("PlayStation 5 Pro", Tipo.CONSOLE, "Playstation", "2 TB • sem leitor de disco", 6999.90, "ps5_pro.png", 4),
            // ---- XBOX — CONSOLES ----
            novo("Xbox 360 Slim", Tipo.CONSOLE, "Xbox", "250 GB", 999.90, "xbox_360_slim.png", 5),
            novo("Xbox One", Tipo.CONSOLE, "Xbox", "500 GB", 1299.90, "xbox_one.png", 6),
            novo("Xbox One S", Tipo.CONSOLE, "Xbox", "1 TB • 4K", 1799.90, "xbox_one_s.png", 8),
            novo("Xbox One X", Tipo.CONSOLE, "Xbox", "1 TB • 4K nativo", 2799.90, "xbox_one_x.png", 5),
            novo("Xbox Series S", Tipo.CONSOLE, "Xbox", "512 GB • sem leitor de disco", 2799.90, "xbox_series_s.png", 10),
            novo("Xbox Series X", Tipo.CONSOLE, "Xbox", "1 TB • com leitor de disco", 4799.90, "xbox_series_x.png", 10),
            // ---- NINTENDO — CONSOLES ----
            novo("Nintendo Switch", Tipo.CONSOLE, "Nintendo", "32 GB • portátil e TV", 2299.90, "switch.png", 9),
            novo("Nintendo Switch OLED", Tipo.CONSOLE, "Nintendo", "64 GB • tela OLED de 7\"", 2699.90, "switch_oled.png", 8),
            novo("Nintendo Switch Lite", Tipo.CONSOLE, "Nintendo", "32 GB • somente portátil", 1699.90, "switch_lite.png", 8),
            novo("Nintendo Switch 2", Tipo.CONSOLE, "Nintendo", "256 GB • tela de 7,9\"", 4499.90, "switch_2.png", 5),
            // ---- PC GAMER ----
            novo("PC Gamer Entrada", Tipo.COMPUTADOR, "PC", "Ryzen 5 5600G • 16 GB • SSD 500 GB", 2999.90, "pc_entrada.png", 6),
            novo("PC Gamer Intermediário", Tipo.COMPUTADOR, "PC", "Ryzen 5 • RTX 4060 • 16 GB • SSD 1 TB", 5499.90, "pc_intermediario.png", 5),
            novo("PC Gamer Avançado", Tipo.COMPUTADOR, "PC", "Ryzen 7 • RTX 4070 Super • 32 GB • SSD 1 TB", 9999.90, "pc_avancado.png", 3),
            novo("Notebook Gamer", Tipo.COMPUTADOR, "PC", "Core i5 • RTX 4050 • 16 GB • SSD 512 GB", 6499.90, "notebook_gamer.png", 5),
            // ---- CONTROLES — PLAYSTATION ----
            novo("DualShock 2", Tipo.CONTROLE, "Playstation", "Para PlayStation 2", 149.90, "dualshock_2.png", 8),
            novo("DualShock 3", Tipo.CONTROLE, "Playstation", "Para PlayStation 3", 199.90, "dualshock_3.png", 8),
            novo("DualShock 4", Tipo.CONTROLE, "Playstation", "Para PlayStation 4", 349.90, "dualshock_4.png", 12),
            novo("DualSense", Tipo.CONTROLE, "Playstation", "Para PlayStation 5", 449.90, "dualsense.png", 15),
            novo("DualSense Edge", Tipo.CONTROLE, "Playstation", "Para PlayStation 5 • profissional", 1299.90, "dualsense_edge.png", 4),
            // ---- CONTROLES — XBOX ----
            novo("Controle Xbox 360 sem fio", Tipo.CONTROLE, "Xbox", "Para Xbox 360", 199.90, "controle_xbox_360.png", 8),
            novo("Controle Xbox One", Tipo.CONTROLE, "Xbox", "Para Xbox One", 399.90, "controle_xbox_one.png", 10),
            novo("Controle Xbox Series", Tipo.CONTROLE, "Xbox", "Para Xbox Series X|S e One • Bluetooth", 449.90, "controle_xbox_series.png", 15),
            novo("Xbox Elite Series 2", Tipo.CONTROLE, "Xbox", "Para Xbox Series X|S e One • profissional", 1499.90, "xbox_elite_2.png", 3),
            // ---- CONTROLES — NINTENDO ----
            novo("Joy-Con (par)", Tipo.CONTROLE, "Nintendo", "Para Switch", 649.90, "joycon_par.png", 10),
            novo("Pro Controller", Tipo.CONTROLE, "Nintendo", "Para Switch", 499.90, "pro_controller.png", 8),
            novo("Joy-Con 2 (par)", Tipo.CONTROLE, "Nintendo", "Para Switch 2", 699.90, "joycon_2_par.png", 6),
            novo("Pro Controller Switch 2", Tipo.CONTROLE, "Nintendo", "Para Switch 2", 599.90, "pro_controller_2.png", 6),
            // ---- CONTROLES — PC ----
            novo("Logitech F310", Tipo.CONTROLE, "PC", "Para PC • com fio • USB", 199.90, "logitech_f310.png", 9),
            novo("8BitDo Pro 2", Tipo.CONTROLE, "PC", "Para PC • Bluetooth • também Switch e Android", 399.90, "8bitdo_pro_2.png", 7),
            novo("Controle Xbox + Adaptador para PC", Tipo.CONTROLE, "PC", "Para PC • sem fio • adaptador USB incluso", 649.90, "xbox_pc_adaptador.png", 6)
    );

    private CatalogoAcessorios() {}

    public static boolean contem(Produto produto) {
        return produto instanceof Acessorio && TODOS.contains(produto);
    }

    private static Acessorio novo(String nome, Tipo tipo, String plataforma, String detalhe,
                                  double preco, String imagem, int estoque) {
        return new Acessorio(nome, tipo, plataforma, detalhe, preco, caminhoImagem(imagem), estoque);
    }

    // Devolve null se o arquivo não existir (o card mostra o texto "FOTO" no lugar)
    private static String caminhoImagem(String arquivo) {
        URL url = CatalogoAcessorios.class.getResource("/com/senai/javafx/imagens/acessorios/" + arquivo);
        return url != null ? url.toExternalForm() : null;
    }
}

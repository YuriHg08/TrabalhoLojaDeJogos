package com.senai.javafx.entidades;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;

public class GerenciamentoJanela extends Application {

    private static Stage primaryStage;
    private static boolean modoEscuroAtivo = false;
    private static PauseTransition delayNotificacaoAtual;

    public enum TipoNotificacao {
        SUCESSO,
        ERRO,
        ALERTA,
        INFO
    }

    public enum TipoAlerta {
        INFO("Aviso", "#00f0ff"),
        SUCESSO("Sucesso", "#00ff88"),
        ALERTA("Atenção", "#ffc700"),
        ERRO("Erro", "#e53935");

        final String tituloPadrao;
        final String corDestaque;

        TipoAlerta(String tituloPadrao, String corDestaque) {
            this.tituloPadrao = tituloPadrao;
            this.corDestaque = corDestaque;
        }
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.initStyle(StageStyle.UNDECORATED);

        abrirJanela("/com/senai/javafx/opcaoUser.fxml", "Nexus");
    }

    /**
     * Exibe uma barra de notificação animada (Toast) no Label da tela sem interromper o fluxo do usuário.
     */
    public static void mostrarNotificacao(Label lblNotificacao, String mensagem, TipoNotificacao tipo) {
        if (lblNotificacao == null) return;

        // Interrompe animação anterior se uma nova notificação for chamada rapidamente
        if (delayNotificacaoAtual != null) {
            delayNotificacaoAtual.stop();
        }

        // Reseta classes de estilo e aplica a classe correspondente ao tipo
        lblNotificacao.getStyleClass().removeAll(
                "barra-notificacao",
                "barra-notificacao-sucesso",
                "barra-notificacao-erro",
                "barra-notificacao-alerta"
        );
        lblNotificacao.getStyleClass().add("barra-notificacao");

        switch (tipo) {
            case SUCESSO -> lblNotificacao.getStyleClass().add("barra-notificacao-sucesso");
            case ERRO -> lblNotificacao.getStyleClass().add("barra-notificacao-erro");
            case ALERTA -> lblNotificacao.getStyleClass().add("barra-notificacao-alerta");
            default -> lblNotificacao.getStyleClass().add("barra-notificacao-info");
        }

        lblNotificacao.setText(mensagem);
        lblNotificacao.setOpacity(1.0);
        lblNotificacao.setVisible(true);
        lblNotificacao.setManaged(true);

        // Exibe por 2.5 segundos e faz o Fade Out suave em 0.5s
        delayNotificacaoAtual = new PauseTransition(Duration.seconds(2.5));
        delayNotificacaoAtual.setOnFinished(event -> {
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), lblNotificacao);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> {
                lblNotificacao.setVisible(false);
                lblNotificacao.setManaged(false);
            });
            fadeOut.play();
        });

        delayNotificacaoAtual.play();
    }

    public static void abrirJanela(String fxmlPath, String titulo) {
        try {
            URL fxmlUrl = GerenciamentoJanela.class.getResource(fxmlPath);
            if (fxmlUrl == null) {
                System.err.println("Arquivo FXML não encontrado: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            if (primaryStage == null) {
                primaryStage = new Stage();
            }

            Scene scene = primaryStage.getScene();

            if (scene == null) {
                scene = new Scene(root);

                URL menuCssUrl = GerenciamentoJanela.class.getResource("/com/senai/javafx/styleMenu.css");
                if (menuCssUrl != null) {
                    scene.getStylesheets().add(menuCssUrl.toExternalForm());
                }
                primaryStage.setScene(scene);
            } else {
                scene.setRoot(root);
            }

            primaryStage.setTitle(titulo);

            // Ajusta a janela ao tamanho exato da tela livre (respeitando a barra de tarefas)
            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setX(visualBounds.getMinX());
            primaryStage.setY(visualBounds.getMinY());
            primaryStage.setWidth(visualBounds.getWidth());
            primaryStage.setHeight(visualBounds.getHeight());

            if (!primaryStage.isShowing()) {
                primaryStage.show();
            }

        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela: " + fxmlPath);
            e.printStackTrace();
            if (primaryStage != null && primaryStage.isShowing()) {
                exibirAlerta("Erro", "Não foi possível abrir a tela.\n" + e.getMessage(), TipoAlerta.ERRO);
            }
        }
    }

    public static void voltarTela() {
        abrirJanela("/com/senai/javafx/opcao.fxml", "Menu de opções");
    }

    // Tela que fica guardada enquanto o usuário vê os detalhes de um jogo
    private static Parent telaGuardada;
    private static String tituloGuardado;

    public static void guardarTela(Parent root) {
        telaGuardada = root;
        tituloGuardado = primaryStage != null ? primaryStage.getTitle() : null;
    }

    public static boolean temTelaGuardada() {
        return telaGuardada != null;
    }

    public static boolean restaurarTelaGuardada(String titulo) {
        if (telaGuardada == null || primaryStage == null || primaryStage.getScene() == null) {
            return false;
        }
        primaryStage.getScene().setRoot(telaGuardada);
        primaryStage.setTitle(tituloGuardado != null ? tituloGuardado : titulo);
        telaGuardada = null;
        tituloGuardado = null;
        return true;
    }

    /**
     * Exibe um modal customizado integrado ao tema visual da aplicação.
     */
    public static void exibirAlerta(String titulo, String mensagem, TipoAlerta tipo) {
        Stage modalStage = new Stage();

        modalStage.initModality(Modality.APPLICATION_MODAL);
        if (primaryStage != null) {
            modalStage.initOwner(primaryStage);
        }
        modalStage.initStyle(StageStyle.UNDECORATED);
        modalStage.setResizable(false);

        // --- BARRA SUPERIOR (TÍTULO E FECHAR) ---
        Label lblTitulo = new Label(titulo != null && !titulo.isBlank() ? titulo : tipo.tituloPadrao);
        lblTitulo.setStyle(
                "-fx-text-fill: " + tipo.corDestaque + "; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 14px;"
        );

        Region espacador = new Region();
        HBox.setHgrow(espacador, Priority.ALWAYS);

        Button btnFechar = new Button("✕");
        btnFechar.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: -cor-texto-secundario; " +
                        "-fx-font-size: 13px; " +
                        "-fx-cursor: hand;"
        );
        btnFechar.setOnAction(e -> modalStage.close());

        HBox barraSuperior = new HBox(lblTitulo, espacador, btnFechar);
        barraSuperior.setAlignment(Pos.CENTER_LEFT);
        barraSuperior.setPadding(new Insets(12, 16, 8, 16));

        // --- CORPO DA MENSAGEM ---
        Label lblMensagem = new Label(mensagem);
        lblMensagem.setWrapText(true);
        lblMensagem.setMaxWidth(380);
        lblMensagem.setStyle(
                "-fx-text-fill: -cor-texto-principal; " +
                        "-fx-font-size: 13px;"
        );
        lblMensagem.setAlignment(Pos.CENTER);

        VBox corpo = new VBox(lblMensagem);
        corpo.setPadding(new Insets(16, 20, 20, 20));
        corpo.setAlignment(Pos.CENTER);

        // --- BOTÃO DE CONFIRMAÇÃO ---
        Button btnOk = new Button("OK");
        btnOk.getStyleClass().add("botao-primario");
        btnOk.setPrefWidth(90);
        btnOk.setOnAction(e -> modalStage.close());

        HBox rodape = new HBox(btnOk);
        rodape.setAlignment(Pos.CENTER_RIGHT);
        rodape.setPadding(new Insets(8, 20, 16, 20));

        // --- CONTAINER DO DIÁLOGO ---
        VBox layout = new VBox(barraSuperior, corpo, rodape);
        layout.getStyleClass().add("dialogo-custom");

        if (!modoEscuroAtivo) {
            layout.getStyleClass().add("modo-claro");
        }

        final double[] arrasto = new double[2];
        barraSuperior.setOnMousePressed(e -> {
            arrasto[0] = modalStage.getX() - e.getScreenX();
            arrasto[1] = modalStage.getY() - e.getScreenY();
        });
        barraSuperior.setOnMouseDragged(e -> {
            modalStage.setX(e.getScreenX() + arrasto[0]);
            modalStage.setY(e.getScreenY() + arrasto[1]);
        });

        Scene scene = new Scene(layout);
        URL cssUrl = GerenciamentoJanela.class.getResource("/com/senai/javafx/styleMenu.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        modalStage.setScene(scene);

        modalStage.setOnShown(e -> {
            modalStage.centerOnScreen();
        });

        modalStage.showAndWait();
    }
}
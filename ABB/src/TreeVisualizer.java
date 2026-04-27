import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.FileReader;

public class TreeVisualizer extends Application {

    private static final int H_SPACING = 100;
    private static final int V_SPACING = 80;
    private static final int MARGIN = 50;
    private static final int BOX_W = 92;
    private static final int BOX_H = 26;

    private BinarySearchTree bst = new BinarySearchTree();
    private Canvas canvas = new Canvas(800, 400);
    private Label statusLabel = new Label("Pronto.");

    @Override
    public void start(Stage stage) {
        loadCSV();

        TextField insertNickField = new TextField();
        insertNickField.setPromptText("Nickname");
        insertNickField.setPrefWidth(130);

        TextField insertRankField = new TextField();
        insertRankField.setPromptText("Ranking");
        insertRankField.setPrefWidth(80);

        Button insertBtn = new Button("Inserir");
        insertBtn.setOnAction(e -> {
            String nick = insertNickField.getText().trim();
            String rankStr = insertRankField.getText().trim();
            if (nick.isEmpty() || rankStr.isEmpty()) {
                statusLabel.setText("Preencha nickname e ranking.");
                return;
            }
            try {
                int rank = Integer.parseInt(rankStr);
                bst.insert(new Player(nick, rank));
                statusLabel.setText("Inserido: " + nick + " (ranking " + rank + ")");
                insertNickField.clear();
                insertRankField.clear();
                redrawTree();
            } catch (NumberFormatException ex) {
                statusLabel.setText("Ranking deve ser um número inteiro.");
            }
        });

        TextField searchRemoveField = new TextField();
        searchRemoveField.setPromptText("Nickname");
        searchRemoveField.setPrefWidth(130);

        Button searchBtn = new Button("Buscar");
        searchBtn.setOnAction(e -> {
            String nick = searchRemoveField.getText().trim();
            if (nick.isEmpty()) {
                statusLabel.setText("Digite um nickname para buscar.");
                return;
            }
            if (bst.search(nick)) {
                statusLabel.setText("Encontrado: " + nick);
            } else {
                statusLabel.setText("Não encontrado: " + nick);
            }
        });

        Button removeBtn = new Button("Remover");
        removeBtn.setOnAction(e -> {
            String nick = searchRemoveField.getText().trim();
            if (nick.isEmpty()) {
                statusLabel.setText("Digite um nickname para remover.");
                return;
            }
            Player removed = bst.remove(nick);
            if (removed != null) {
                statusLabel.setText("Removido: " + removed.getNickname() + " (ranking " + removed.getRanking() + ")");
                searchRemoveField.clear();
                redrawTree();
            } else {
                statusLabel.setText("Não encontrado: " + nick);
            }
        });

        HBox insertRow = new HBox(8, new Label("Inserir:"), insertNickField, insertRankField, insertBtn);
        insertRow.setAlignment(Pos.CENTER_LEFT);
        insertRow.setPadding(new Insets(4));

        HBox searchRow = new HBox(8, new Label("Buscar / Remover:"), searchRemoveField, searchBtn, removeBtn);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        searchRow.setPadding(new Insets(4));

        VBox bottomPane = new VBox(6, insertRow, searchRow, statusLabel);
        bottomPane.setPadding(new Insets(10));
        bottomPane.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 1 0 0 0;");

        Pane canvasPane = new Pane(canvas);
        ScrollPane scrollPane = new ScrollPane(canvasPane);
        scrollPane.setFitToHeight(false);
        scrollPane.setFitToWidth(false);

        BorderPane root = new BorderPane();
        root.setCenter(scrollPane);
        root.setBottom(bottomPane);

        redrawTree();

        Scene scene = new Scene(root, 1200, 720);
        stage.setTitle("Ranking de Jogadores - Árvore Binária de Busca");
        stage.setScene(scene);
        stage.show();
    }

    private void loadCSV() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("players.csv"));
            int count = 0;
            while (br.readLine() != null) count++;
            br.close();

            String[] lines = new String[count];
            br = new BufferedReader(new FileReader("players.csv"));
            for (int i = 0; i < count; i++) lines[i] = br.readLine();
            br.close();

            for (int i = 1; i < count; i++)  {
                String[] parts = lines[i].split(",");
                String nickname = parts[0].trim();
                int ranking = Integer.parseInt(parts[1].trim());
                bst.insert(new Player(nickname, ranking));
            }

            statusLabel.setText("CSV carregado: " + (count - 1) + " jogadores inseridos.");
        } catch (Exception e) {
            statusLabel.setText("Erro ao carregar players.csv. Verifique o caminho do arquivo.");
        }
    }

    private void redrawTree() {
        int nodeCount = bst.countNodes();
        int height = bst.getHeight();
        double w = Math.max(900, nodeCount * H_SPACING + 2 * MARGIN);
        double h = Math.max(400, height * V_SPACING + 2 * MARGIN);

        canvas.setWidth(w);
        canvas.setHeight(h);

        bst.assignInorderPositions();

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, w, h);
        gc.setFont(Font.font("Monospaced", 8));

        drawNode(gc, bst.getRoot(), 0);
    }

    private void drawNode(GraphicsContext gc, Node node, int depth) {
        if (node == null) return;

        double x = MARGIN + node.inorderPos * H_SPACING;
        double y = MARGIN + depth * V_SPACING;

        if (node.left != null) {
            double cx = MARGIN + node.left.inorderPos * H_SPACING;
            double cy = MARGIN + (depth + 1) * V_SPACING;
            gc.setStroke(Color.GRAY);
            gc.setLineWidth(1.5);
            gc.strokeLine(x, y, cx, cy);
        }
        if (node.right != null) {
            double cx = MARGIN + node.right.inorderPos * H_SPACING;
            double cy = MARGIN + (depth + 1) * V_SPACING;
            gc.setStroke(Color.GRAY);
            gc.setLineWidth(1.5);
            gc.strokeLine(x, y, cx, cy);
        }

        drawNode(gc, node.left, depth + 1);
        drawNode(gc, node.right, depth + 1);

        gc.setFill(Color.STEELBLUE);
        gc.fillRoundRect(x - BOX_W / 2.0, y - BOX_H / 2.0, BOX_W, BOX_H, 10, 10);
        gc.setStroke(Color.DARKBLUE);
        gc.setLineWidth(1.5);
        gc.strokeRoundRect(x - BOX_W / 2.0, y - BOX_H / 2.0, BOX_W, BOX_H, 10, 10);

        gc.setFill(Color.WHITE);
        gc.fillText(node.player.getNickname(), x - BOX_W / 2.0 + 4, y + 4);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

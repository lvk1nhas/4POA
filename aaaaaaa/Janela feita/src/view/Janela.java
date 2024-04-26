package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import control.Service;

public class Janela extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private JButton btnConsultar;
    private JTextField txtCEP;
    private JTextArea txtResultado;
    private ArrayList<JCheckBox> checkboxes;

    public Janela() {
        super("Consulta de CEP");
        this.setLayout(new BorderLayout());
        this.setSize(300, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Componentes da interface
        txtCEP = new JTextField(10);
        btnConsultar = new JButton("Consultar");
        txtResultado = new JTextArea(10, 20);
        txtResultado.setEditable(false);
        txtResultado.setLineWrap(true);
        txtResultado.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(txtResultado);

        // Adicionando checkboxes
        checkboxes = new ArrayList<>();
        JPanel pnlCheckboxes = new JPanel(new GridLayout(0, 1));
        String[] campos = {" cep", " logradouro", " complemento", " bairro", " localidade", " uf", " ibge", " gia", " ddd", " siafi"};
        for (String campo : campos) {
            JCheckBox checkbox = new JCheckBox(campo);
            checkbox.setSelected(true); // Inicialmente, todos os checkboxes estão marcados
            checkboxes.add(checkbox);
            pnlCheckboxes.add(checkbox);
        }

        // Painel para o campo de entrada do CEP e o botão
        JPanel pnlEntrada = new JPanel();
        pnlEntrada.setLayout(new FlowLayout());
        pnlEntrada.add(new JLabel("CEP:"));
        pnlEntrada.add(txtCEP);
        pnlEntrada.add(btnConsultar);

        // Adicionando componentes à janela
        add(pnlEntrada, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(pnlCheckboxes, BorderLayout.EAST);

        // Adicionando listener ao botão
        btnConsultar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnConsultar) {
            String cep = txtCEP.getText();
            if (!cep.isEmpty()) {
                String resposta = Service.consultarCEP(cep);
                String resultadoFormatado = formatarResultado(resposta);
                txtResultado.setText(resultadoFormatado);
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, insira um CEP.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String formatarResultado(String resposta) {
        // Remover chaves e vírgulas
        resposta = resposta.replace("{", "").replace("}", "").replace(",", "\n");
        // Substituir ':' por ': ' para formatar as informações
        resposta = resposta.replace(": ", ":  ");
        // Remover aspas duplas dos valores
        resposta = resposta.replace("\"", "");

        // Filtrar os campos com base nos checkboxes selecionados
        StringBuilder resultadoFiltrado = new StringBuilder();
        for (JCheckBox checkbox : checkboxes) {
            String campo = checkbox.getText();
            if (checkbox.isSelected() && resposta.contains(campo)) {
                resultadoFiltrado.append(campo).append(": ").append(getValorCampo(resposta, campo)).append("\n");
            }
        }

        return resultadoFiltrado.toString();
    }

    private String getValorCampo(String resposta, String campo) {
        int indexCampo = resposta.indexOf(campo);
        int indexValor = resposta.indexOf(":", indexCampo) + 1;
        int indexProximoCampo = resposta.indexOf("\n", indexValor);
        if (indexProximoCampo == -1) {
            indexProximoCampo = resposta.length();
        }
        return resposta.substring(indexValor, indexProximoCampo).trim();
    }

    public static void exibirJanela() {
        SwingUtilities.invokeLater(() -> {
            Janela interfaceCEP = new Janela();
            interfaceCEP.setVisible(true);
        });
    }

}

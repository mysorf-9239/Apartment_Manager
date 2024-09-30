package view.table;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PaginationPanel extends JPanel {
    private int currentPage;
    private int totalPages;
    private ActionListener pageChangeListener;

    public PaginationPanel(int currentPage, int totalPages, ActionListener pageChangeListener) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.pageChangeListener = pageChangeListener;

        setLayout(new FlowLayout(FlowLayout.CENTER));
        updatePagination();
    }

    public void update(int currentPage, int totalPages) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        updatePagination();
    }

    private void updatePagination() {
        removeAll();

        // Nút < (trang trước)
        JButton prevButton = new JButton("<");
        prevButton.setEnabled(currentPage > 1);
        prevButton.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                pageChangeListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
            }
        });
        add(prevButton);

        // Hiển thị các nút số trang
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, currentPage + 2);

        if (startPage > 1) {
            add(new JLabel("..."));
        }

        for (int i = startPage; i <= endPage; i++) {
            JButton pageButton = new JButton(String.valueOf(i));
            pageButton.setEnabled(i != currentPage);
            pageButton.addActionListener(e -> {
                currentPage = Integer.parseInt(pageButton.getText());
                pageChangeListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
            });
            add(pageButton);
        }

        if (endPage < totalPages) {
            add(new JLabel("..."));
        }

        // Nút > (trang sau)
        JButton nextButton = new JButton(">");
        nextButton.setEnabled(currentPage < totalPages);
        nextButton.addActionListener(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                pageChangeListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
            }
        });
        add(nextButton);

        revalidate();
        repaint();
    }
}

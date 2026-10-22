package com.gudosnest.ui.OwnerHomePanels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.*;

import com.gudosnest.db.DataStore;
import com.gudosnest.ui.Theme;
import com.gudosnest.ui.UIHelpers;

import static com.gudosnest.ui.UIHelpers.*;

/**
 * Payments & Income: per-property financial summary + payment records.
 * Skeleton note: monthly/annual breakdowns, damage fees, refunds next.
 */
public class IncomePanel extends JPanel {
    public IncomePanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        ArrayList<String[]> sums = DataStore.getPropertySummaries();
        JPanel cards = new JPanel(new GridLayout(1, Math.max(1, sums.size()), 14, 0));
        cards.setOpaque(false);
        cards.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (String[] s : sums) {
            long acq = parseMoney(s[1]);
            long inc = parseMoney(s[2]);
            long exp = parseMoney(s[3]);
            long net = inc - exp;
            boolean recovered = net >= acq;

            String txt = "Acquired: " + s[1]
                    + "   |   Income: PHP " + String.format("%,d", inc)
                    + "   |   Expenses: PHP " + String.format("%,d", exp)
                    + "   |   Net: " + (net < 0 ? "-" : "") + "PHP " + String.format("%,d", Math.abs(net))
                    + "   |   Investment: " + (recovered ? "recovered" : "recovering");
            cards.add(summaryCard(s[0], txt, Theme.PRIMARY));
        }

        JPanel incomeCard = cardPanel(new BorderLayout(0, 10), 16);
        incomeCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        incomeCard.add(label("Per-property financial summary (live from FinancialTransactions)", Theme.TEXT, 14), BorderLayout.NORTH);
        incomeCard.add(cards, BorderLayout.CENTER);
        incomeCard.setPreferredSize(new Dimension(100, 150));
        add(incomeCard);
        add(Box.createVerticalStrut(14));

        JPanel pay = cardPanel(new BorderLayout(0, 10), 16);
        pay.setAlignmentX(Component.LEFT_ALIGNMENT);
        pay.add(label("Payment records", Theme.TEXT, 14), BorderLayout.NORTH);
            pay.add(makeTable(DataStore.rowsPayments(),
                new String[]{"Booking", "Amount", "Type", "Status", "Paid at"}), BorderLayout.CENTER);
        pay.setPreferredSize(new Dimension(100, 220));
        add(pay);
        add(Box.createVerticalStrut(12));
        JLabel note = label(
            "Skeleton note: monthly/annual breakdowns, damage-fee requests and"
            + " refund actions are next milestones.",
            Theme.TEXT_MUT, 12);
        note.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(note);
    }
}

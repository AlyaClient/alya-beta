package works.alya.module.impl.utility;

import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import works.alya.event.IEventListener;
import works.alya.event.impl.PacketReceiveEvent;
import works.alya.event.impl.TickEvent;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import works.alya.utilities.misc.ChatUtility;
import net.minecraft.network.packet.Packet;

import java.util.ArrayList;
import java.util.List;

public class AnticheatDetect extends Module {
    private final List<Integer> transactionHistory = new ArrayList<>();
    private long lastTransactionTime = 0;
    private String detectedAnticheat = "Unknown";
    private boolean hasDetected = false;

    public AnticheatDetect() {
        super("AnticheatDetect", "Anticheat Detect", "Able to detect some anti-cheats", ModuleCategory.UTILITY);
    }

    @SuppressWarnings("unused")
    private final IEventListener<PacketReceiveEvent> packetReceiveEvent = event -> {
        Packet<?> packet = event.getPacket();

        Integer transactionId = extractTransactionId(packet);
        if(transactionId != null) {
            onTransactionReceived(transactionId);
        }
    };

    private Integer extractTransactionId(Packet<?> packet) {
        try {
            if(packet instanceof CommonPingS2CPacket pingPacket) {
                int parameter = pingPacket.getParameter();
                ChatUtility.sendDebug("[Transaction ID] " + parameter + " (CommonPing)");
                return parameter;
            }
        } catch(Exception e) {
            ChatUtility.sendDebug("Error extracting transaction ID: " + e.getMessage());
        }

        return null;
    }

    @Override
    public void onEnable() {
        ChatUtility.sendInfo("Trying to detect anticheat, please wait a moment...");
        transactionHistory.clear();
        lastTransactionTime = System.currentTimeMillis();
        detectedAnticheat = "Unknown";
        hasDetected = false;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        transactionHistory.clear();
        super.onDisable();
    }

    public void onTransactionReceived(int transactionId) {
        lastTransactionTime = System.currentTimeMillis();
        transactionHistory.add(transactionId);

        if(transactionHistory.size() > 10) {
            transactionHistory.removeFirst();
        }

        if(transactionHistory.size() >= 5) {
            analyzeTransactionPattern();
        }
    }

    private void analyzeTransactionPattern() {
        if(hasDetected || transactionHistory.size() < 5) return;

        List<Integer> transactions = new ArrayList<>(transactionHistory);

        List<Integer> diffs = new ArrayList<>();
        for(int i = 1; i < transactions.size(); i++) {
            diffs.add(transactions.get(i) - transactions.get(i - 1));
        }

        int first = transactions.get(0);
        String detectedAC = guessAntiCheat(diffs, first, transactions);

        if(detectedAC != null) {
            detectedAnticheat = detectedAC;
            hasDetected = true;
            ChatUtility.sendMessage("§c[AnticheatDetect] §fDetected: §e" + detectedAC);
        }
    }

    private String guessAntiCheat(List<Integer> diffs, int first, List<Integer> transactions) {
        boolean allSameDiff = diffs.stream().allMatch(diff -> diff.equals(diffs.get(0)));

        if(allSameDiff) {
            int firstDiff = diffs.get(0);

            if(firstDiff == 1) {
                if(first >= -23772 && first <= -23762) return "Vulcan";
                if((first >= 95 && first <= 105) || (first >= -20005 && first <= -19995)) return "Matrix";
                if(first >= -32773 && first <= -32762) return "Grizzly";
                return "Verus";
            }

            if(firstDiff == -1) {
                if(first >= -8287 && first <= -8280) return "Errata";
                if(first < -3000) return "Intave";
                if(first >= -5 && first <= 0) return "Grim";
                if(first >= -3000 && first <= -2995) return "Karhu";
                return "Polar";
            }
        }

        if(transactions.size() >= 4 &&
                transactions.get(0).equals(transactions.get(1))) {
            boolean restIncrementing = true;
            for(int i = 2; i < diffs.size(); i++) {
                if(diffs.get(i) != 1) {
                    restIncrementing = false;
                    break;
                }
            }
            if(restIncrementing) return "Verus";
        }

        if(diffs.size() >= 3 &&
                diffs.get(0) >= 100 && diffs.get(1) == -1) {
            boolean restNegativeOne = true;
            for(int i = 2; i < diffs.size(); i++) {
                if(diffs.get(i) != -1) {
                    restNegativeOne = false;
                    break;
                }
            }
            if(restNegativeOne) return "Polar";
        }

        if(first < -3000 && transactions.contains(0)) return "Intave";

        if(transactions.size() >= 6 &&
                transactions.get(0) == -30767 &&
                transactions.get(1) == -30766 &&
                transactions.get(2) == -25767) {
            boolean restIncrementing = true;
            for(int i = 3; i < diffs.size(); i++) {
                if(diffs.get(i) != 1) {
                    restIncrementing = false;
                    break;
                }
            }
            if(restIncrementing) return "Old Vulcan";
        }

        return null;
    }

    @SuppressWarnings("unused")
    private final IEventListener<TickEvent> onTickEvent = event -> {
        if(System.currentTimeMillis() - lastTransactionTime > 10000 && !transactionHistory.isEmpty()) {
            if(!hasDetected) {
                detectedAnticheat = "Unknown";
                ChatUtility.sendMessage("§c[AnticheatDetect] §fDetected: §eUnknown (No transactions for 10s)");
                hasDetected = true;
            }
        }
    };

    @SuppressWarnings("unused")
    public String getDetectedAnticheat() {
        return detectedAnticheat;
    }

    @SuppressWarnings("unused")
    public void setDetectedAnticheat(String detectedAnticheat) {
        this.detectedAnticheat = detectedAnticheat;
    }
}
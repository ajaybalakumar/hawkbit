package org.eclipse.hawkbit.ui.rollout;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.hawkbit.repository.model.TotalTargetCountStatus.Status;
import org.vaadin.alump.distributionbar.gwt.client.GwtDistributionBar;

/**
 * Distribution bar helper to render distribution bar in grid.
 * 
 */
public final class DistributionBarHelper {
    private static final int PARENT_SIZE_IN_PCT = 100;
    private static final double MINIMUM_PART_SIZE = 10;
    private static final String DISTRIBUTION_BAR_PART_MAIN_STYLE = GwtDistributionBar.CLASSNAME + "-part";
    private static final String DISTRIBUTION_BAR_PART_CLASSNAME_PREFIX = GwtDistributionBar.CLASSNAME + "-part-";
    private static final String DISTRIBUTION_BAR_PART_VALUE_CLASSNAME = GwtDistributionBar.CLASSNAME + "-value";
    private static final String UNINITIALIZED_VALUE_CLASSNAME = GwtDistributionBar.CLASSNAME + "-uninitizalized";

    private DistributionBarHelper() {
    }

    private static String getPartStyle(int partIndex, int noOfParts, String customStyle) {
        StringBuilder mainStyle = new StringBuilder();
        StringBuilder styleName = new StringBuilder(GwtDistributionBar.CLASSNAME);
        if (noOfParts == 1) {
            styleName.append("-only");
        } else if (partIndex == 1) {
            styleName.append("-left");
        } else if (partIndex == noOfParts) {
            styleName.append("-right");
        } else {
            styleName.append("-middle");
        }
        mainStyle.append(styleName).append(" ");
        mainStyle.append(DISTRIBUTION_BAR_PART_MAIN_STYLE).append(" ");
        mainStyle.append(DISTRIBUTION_BAR_PART_CLASSNAME_PREFIX + partIndex);
        if (customStyle != null) {
            mainStyle.append(" ").append("status-bar-part-" + customStyle);
        }
        return mainStyle.toString();
    }

    private static String getPartWidth(Long value, Long totalValue, int noOfParts) {
        final double minTotalSize = MINIMUM_PART_SIZE * noOfParts;
        final double availableSize = PARENT_SIZE_IN_PCT - minTotalSize;
        double val = MINIMUM_PART_SIZE + (double) value / totalValue * availableSize;
        return String.format("%.3f", val) + "%";
    }

    private static String getPart(int partIndex, Status status, Long value, Long totalValue, int noOfParts) {
        String partValue = status.toString().toLowerCase();
        // return "<div class=\"" + getPartStyle(partIndex, noOfParts,
        // partValue) + "\" style=\"width: "
        // + getPartWidth(value, totalValue, noOfParts) + ";\" title = \"" +
        // partValue + "\"><span class=\""
        // + DISTRIBUTION_BAR_PART_VALUE_CLASSNAME + "\">" + value +
        // "</span></div>";
        return "<div class=\"" + getPartStyle(partIndex, noOfParts, partValue) + "\" style=\"width: "
                + getPartWidth(value, totalValue, noOfParts) + ";\"><span class=\""
                + DISTRIBUTION_BAR_PART_VALUE_CLASSNAME + "\">" + value + "</span></div>";
    }

    public static String getDistributionBarAsHTMLString(Map<Status, Long> statusTotalCountMap) {
        StringBuilder htmlString = new StringBuilder();
        htmlString.append(getParentDivStart());
        Long totalValue = getTotalSizes(statusTotalCountMap);
        Map<Status, Long> statusMapWithNonZeroValues = getNonZeroStatusList(statusTotalCountMap);

        if (statusMapWithNonZeroValues.size() > 0) {
            int partIndex = 1;
            for (Map.Entry<Status, Long> entry : statusMapWithNonZeroValues.entrySet()) {
                if (entry.getValue() > 0) {
                    htmlString.append(getPart(partIndex, entry.getKey(), entry.getValue(), totalValue,
                            statusMapWithNonZeroValues.size()));
                    partIndex++;
                }
            }
        } else {
            return getUnintialisedBar();
        }
        htmlString.append(getParentDivEnd());
        return htmlString.toString();
    }

    public static Map<Status, Long> getNonZeroStatusList(Map<Status, Long> statusTotalCountMap) {
        return statusTotalCountMap.entrySet().stream().filter(p -> p.getValue() > 0)
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
    }


    public static String getTooltip(Map<Status, Long> statusCountMap) {
        Map<Status, Long> nonZeroStatusCountMap = DistributionBarHelper.getNonZeroStatusList(statusCountMap);
        StringBuilder tooltip = new StringBuilder();
        for (Entry<Status, Long> entry : nonZeroStatusCountMap.entrySet()) {
            tooltip.append(entry.getKey().toString().toLowerCase()).append(" : ").append(entry.getValue())
                    .append("<br>");
        }
        return tooltip.toString();
    }

    private static String getUnintialisedBar() {
        return "<div class=\"" + UNINITIALIZED_VALUE_CLASSNAME + "\" style=\"width: 100%;\"><span class=\""
                + DISTRIBUTION_BAR_PART_VALUE_CLASSNAME + "\">uninitialized</span></div>";
    }

    private static Long getTotalSizes(Map<Status, Long> statusTotalCountMap) {
        Long total = 0L;
        for (Long value : statusTotalCountMap.values()) {
            total = total + value;
        }
        return total;
    }

    private static String getParentDivStart() {
        return "<div class=\"" + GwtDistributionBar.CLASSNAME
                + "\" style=\"width: 100%; height: 100%;\" id=\"rollout.status.progress.bar.id\">";
    }

    private static String getParentDivEnd() {
        return "</div>";
    }

}

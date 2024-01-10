package bigdata;

public enum SummaryDateType {
    NONE, YEARLY, MONTHLY;

    public static SummaryDateType getDateType(int dateType) {
        if (dateType == SummaryDateType.NONE.ordinal()) return SummaryDateType.NONE;
        if (dateType == SummaryDateType.YEARLY.ordinal()) return SummaryDateType.YEARLY;
        if (dateType == SummaryDateType.MONTHLY.ordinal()) return SummaryDateType.MONTHLY;
        return SummaryDateType.NONE;
    }
}

package bigdata;

public enum SummaryDateType {
    NONE, MONTHLY, WEEKLY;

    public static SummaryDateType getDateType(int dateType) {
        if (dateType == SummaryDateType.NONE.ordinal()) return SummaryDateType.NONE;
        if (dateType == SummaryDateType.MONTHLY.ordinal()) return SummaryDateType.MONTHLY;
        if (dateType == SummaryDateType.WEEKLY.ordinal()) return SummaryDateType.WEEKLY;
        return SummaryDateType.NONE;
    }
}

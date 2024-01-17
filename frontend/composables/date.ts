import { getISOWeek, getMonth, getYear } from 'date-fns';
import { DateType } from '~/models/deckSummary';

export function getDateType(dateType: string): DateType{
    switch(dateType){
        case "NONE":
            return DateType.NONE;
        case "MONTHLY":
            return DateType.MONTHLY;
        case "WEEKLY":
            return DateType.WEEKLY;
        default:
            return DateType.NONE;
    }
}

export function generateDateKey(dateString: string, dateType: DateType): string{
    const date = new Date(dateString);
    const year = getYear(date);
    switch(dateType){
        case DateType.NONE:
            return "N";
        case DateType.MONTHLY:
            return "M_" + year + "-" + getMonth(date);
        case DateType.WEEKLY:
            return "W_" + year + "-" + getISOWeek(date);
    }
}

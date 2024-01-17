<script setup lang="ts">
import { type ApiBody } from '~/models/api';
import { MAXDECKLISTSIZE, type DeckSummary } from '~/models/deckSummary';
import type { DateData } from '~/models/topk';

const page = ref(1)
const isDate = ref(false)

let deck_list: Ref<DeckSummary[][]> = ref([])

const { data } = await useFetch<ApiBody>("/api/data")

const yearList: DateData[] = data.value?.content;

const years: number[] = yearList.map((value: DateData) => {return value.year;});
const year = ref(years[0]);

const dateData = ref(getDateData(year.value, yearList));

const timeRange = ref("monthly");
const sortType = ref('wr');

const months = dateData.value.months;
const month = ref(months[0]);

const weeks = dateData.value.weeks;
const week = ref(weeks[0]);

const fetchData = async () => {
    const year_value = (isDate.value) ? year.value : "";
    const value = (isDate.value) ? ((timeRange.value == "weekly") ? week.value : month.value) : "";
    const dateType = (!isDate.value) ? "N" : ((timeRange.value == "weekly") ? "W" : "M");
    const { data } = await useFetch<ApiBody>(`/api/deck/${dateType}_${year_value}_${value}_${sortType.value}`);
    deck_list.value = (data.value?.content) ? data.value?.content : [];
};

watch([month, week, isDate, timeRange, sortType], async () => {
    fetchData()
});

watch([year],async () => {
    dateData.value = getDateData(year.value, yearList);
})

function getDeckPage(deckPage: DeckSummary[]) {
    return (deckPage) ? deckPage : []
}

function getDateData(year: number, list: DateData[]){
    let data: DateData = {
        year: 0,
        weeks: [],
        months: []
    };
    list.map((value) => {
        if (value.year == year) data = value;
    });
    return data;
}

onMounted(fetchData);
</script>

<template>
    <div class="flex flex-col items-center h-[81vh]">
        <div class="flex flex-row items-center content-start gap-4 w-full p-5 pl-20">
            <URadioGroup
                class="bg-white p-2 rounded-lg"
                legend="Sort by:"
                :options="[{value: 'wr', label: 'Winrate'}, {value: 'mdf', label: 'Mean difference force'}, {value: 'rating', label: 'Rating (wr x mdf)'}]"
                v-model="sortType"/>
            <UToggle v-model="isDate" size="xl" />
            <USelect v-model="year" :options="years" placeholder="Select a year" :disabled="!isDate" size="xl" />
            <URadioGroup
                class="bg-white p-2 rounded-lg"
                legend="Time range:"
                :options="[{value: 'monthly', label: 'Monthly'}, {value: 'weekly', label: 'Weekly'}]"
                v-model="timeRange"
                :disabled="!isDate"/>
            <USelect v-if="timeRange == 'monthly'" v-model="month" :options="months" placeholder="Select a month" :disabled="!isDate || !year" size="xl" />
            <USelect v-if="timeRange == 'weekly'" v-model="week" :options="weeks" placeholder="Select a week" :disabled="!isDate || !year" size="xl" />

        </div>
        <DecksList :deck_list="getDeckPage(deck_list[page - 1])" />
        <div class="flex flex-row items-center gap-4">
            <UPagination :key="deck_list.toString()" v-model="page" :page-count="MAXDECKLISTSIZE"
            :total="deck_list.length * MAXDECKLISTSIZE" size="xl" />

        </div>
    </div>
</template>

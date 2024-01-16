<script setup lang="ts">
import { type ApiBody } from '~/models/api';
import { MAXDECKLISTSIZE, type DeckSummary } from '~/models/deckSummary';

const page = ref(1)
const isDate = ref(false)
const isMonth = ref(false)

let deck_list: Ref<DeckSummary[][]> = ref([])

const years = ["2023"];
const year = ref();

const months = ["09", "10", "11"];
const month = ref();

const fetchData = async () => {
    const year_value = (isDate.value && year.value) ? year.value : ""
    const month_value = (isDate.value && isMonth.value && year_value != "" && month.value) ? "_" + month.value : ""
    const { data } = await useFetch<ApiBody>(`/api/deck/${year_value}${month_value}`);
    deck_list.value = (data.value?.content) ? data.value?.content : [];
};

// Observer les changements de year et month
watch([year, month, isDate, isMonth], async () => {
    fetchData()
});

function getDeckPage(deckPage: DeckSummary[]) {
    return (deckPage) ? deckPage : []
}

onMounted(fetchData);

</script>

<template>
    <div class="flex flex-col items-center h-[81vh]">
        <div class="flex flex-row items-center content-start gap-4 w-full p-5 pl-20">
            <UToggle v-model="isDate" size="xl" />
            <USelect v-model="year" :options="years" placeholder="Select a year" :disabled="!isDate" size="xl" />
            <UToggle v-model="isMonth" size="xl" :disabled="!isDate" />
            <USelect v-model="month" :options="months" placeholder="Select a month" :disabled="!(isDate && isMonth)"
                size="xl" />
        </div>
        <DecksList :deck_list="getDeckPage(deck_list[page - 1])" />
        <div class="flex flex-row items-center gap-4">
            <UPagination :key="deck_list.toString()" v-model="page" :page-count="MAXDECKLISTSIZE"
                :total="deck_list.length * MAXDECKLISTSIZE" size="xl" />

        </div>
    </div>
</template>

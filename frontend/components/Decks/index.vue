<script setup lang="ts">
import { type ApiBody } from '~/models/api';
import { MAXDECKLISTSIZE, type DeckSummary } from '~/models/deckSummary';

const page = ref(1)

let deck_list: DeckSummary[][] = []

const years = ["2023"];
const year = ref(years[0]);

const months = ["09", "10", "11"];
const month = ref(months[0]);

const { data } = await useFetch<ApiBody>(`/api/deck/${year.value}_${month.value}`);
deck_list = (data.value?.content) ? data.value?.content : [];



</script>

<template>
    <div class="flex flex-col items-center h-[81vh]">
        <DecksList :deck_list="deck_list[page - 1]"/>
        <div class="flex flex-row gap-4">
            <UPagination :key="deck_list.toString()" v-model="page" :page-count="MAXDECKLISTSIZE" :total="deck_list.length*MAXDECKLISTSIZE" size="xl"/>
            <USelect v-model="year" :options="years" placeholder="Select a year" size="xl"/>
            <USelect v-model="month" :options="months" placeholder="Select a month" size="xl"/>
        </div>
    </div>

</template>

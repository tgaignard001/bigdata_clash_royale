<script setup lang="ts">
import { MAXDECKLISTSIZE, type DeckSummary } from '~/models/deckSummary';

const page = ref(1)

let deck_list: DeckSummary[][] = []

const { data } = await useFetch('/api/getDecks');
deck_list = (data.value?.content) ? data.value?.content : [];
console.log(deck_list[page.value], page);


</script>

<template>
    <div class="flex flex-col items-center h-[81vh]">
        <DecksList :deck_list="deck_list[page - 1]"/>
        <UPagination v-model="page" :page-count="MAXDECKLISTSIZE" :total="deck_list.length*MAXDECKLISTSIZE" size="xl"/>
    </div>

</template>

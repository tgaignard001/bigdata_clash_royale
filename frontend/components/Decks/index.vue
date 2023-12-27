<script setup lang="ts">
import { MAXDECKLISTSIZE, type DeckSummary } from '~/models/deckSummary';

const page = ref(1)
const items = ref(Array(55));
for (let i =0; i< items.value.length; i++){
    items.value[i] = {id: i, value: i*2}
}

let deck_list: DeckSummary[][] = []

const { data } = await useFetch('/api/getDecks');
deck_list = (data.value?.content) ? data.value?.content : [];

</script>

<template>
    <div class="flex flex-col items-center h-[81vh]">
        <DecksList :deck_list="deck_list[page]"/>
        <UPagination v-model="page" :page-count="MAXDECKLISTSIZE" :total="deck_list.length" size="xl"/>
    </div>

</template>

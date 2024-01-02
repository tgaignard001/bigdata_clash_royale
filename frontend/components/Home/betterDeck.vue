<script setup lang="ts">
import type { ApiBody } from '~/models/api';
import { type DeckSummary, EMPTY_DECK_SUMMARY } from '~/models/deckSummary';

    const { data } = await useFetch<ApiBody>('/api/deck/getBetter');

    const better_deck: DeckSummary = (data.value?.content) ? data.value?.content : EMPTY_DECK_SUMMARY;

</script>
<template>
    <div class="flex flex-col items-center">
        <div>
            Best deck of the dataset
        </div>
        <DecksDeck :deck-summary="better_deck" />
    </div>
</template>
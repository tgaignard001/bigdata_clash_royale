<script setup lang="ts">
import type { DeckSummary } from '~/models/deckSummary';

const props = defineProps<{deck_list: DeckSummary[]}>();

</script>

<template>
    <div :key="deck_list.toString()" class="flex flex-col gap-4 m-4 h-full overflow-auto">
        <div v-for="deck in deck_list" class="flex flex-row items-center">
            <DecksDeck :key="deck.strDeck" :deck-summary="deck"/>
        </div>
    </div>
</template>

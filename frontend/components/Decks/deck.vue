<script setup lang="ts">
import type { DeckSummary } from "~/models/deckSummary";

const props = defineProps<{deckSummary: DeckSummary}>();
const deck = new Deck(props.deckSummary.strDeck)

const columns = [{
    key: 'victories',
    label: 'Victories'
}, {
    key: 'games',
    label: 'Games',
}, {
    key: 'unique_players',
    label: 'Unique Players',
}, {
    key: 'highest_clan',
    label: 'Highest Clan'
}, {
    key: 'mean_winning_force',
    label: 'Mean winning force',
}]
const data = [{
    victories: props.deckSummary.victories,
    games: props.deckSummary.uses,
    unique_players: props.deckSummary.uniquePlayers,
    highest_clan: props.deckSummary.highestClanLevel,
    mean_winning_force: (props.deckSummary.sumDiffForce/props.deckSummary.nbDiffForce).toFixed(2)
}]

</script>
<template>
    <div class="flex flex-row gap-2 border-4 border-black p-4 m-4 bg-white items-center">
        <div class="flex flex-row content-center items-center">
            <div v-for="card in deck.cards()" class="flex flex-col items-center">
                {{ card[0] }}
                <NuxtImg :src="card[1]" placeholder="/card-champion-unknown.png"/>
            </div>
        </div>
        <UTable :rows="data" :columns="columns" class="text-center"/>
    </div>

</template>
<script setup lang="ts">

const props = defineProps(["strDeck"]);
const deck = new Deck(props.strDeck)
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
    victories: 0,
    games: 0,
    unique_players: 0,
    highest_clan: 0,
    mean_winning_force: 0
}]

function handleImageError(card: any) {
    card[1] = "~/public/card-champion-unknown.png";
}
</script>
<template>
    <div class="flex flex-row gap-2 border-4 border-black p-4 bg-white">
        <div class="flex flex-row content-center items-center">
            <div v-for="card in deck.cards()" class="flex flex-col items-center">
                {{ card[0] }}
                <img :src="card[1]" onerror="this.src='/card-champion-unknown.png'"/>
            </div>
        </div>
        <UTable :rows="data" :columns="columns" class="w-3/5"/>
    </div>

</template>
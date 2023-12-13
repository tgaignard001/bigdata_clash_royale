<script setup lang="ts">
    import { Deck } from "~/utils/deck"
    const deck_list = [new Deck("0117006d0f375b4d"), new Deck("1569280b532b6245"), new Deck("184a0e6850406c00"), new Deck("0e032d2905605362")]
</script>

<template>
    <div class="m-4 bg-white flex-grow">
        Vous retrouverez ici les decks les plus joués

        <div v-for="deck in deck_list" class="flex flex-row bg-blue-700 m-2">
            <div v-for="card in deck.cards()" class="bg-green-700">
                {{ card[0] }}
                <img :src="card[1]"/>
            </div>
        </div>
    </div>
</template>

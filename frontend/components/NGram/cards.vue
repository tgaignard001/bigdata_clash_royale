<script setup lang="ts">
import  { cr_cards, type Card } from "~/models/cards";

const props = defineProps<{cards: Card[] }>();
const isOpen = ref([false, false, false, false, false, false, false, false]);

function addCard() {
    let index = Math.round(Math.random()*cr_cards.length)
    while(props.cards.includes(cr_cards[index])){
        index = Math.round(Math.random()*cr_cards.length);
    }
    props.cards.push(cr_cards[index]);
}

function removeCard(index: number){
    return () => {
        props.cards.splice(index, 1);
    }
}

function changeCard(index: number){
    return (card: Card) => {
        props.cards[index] = card;
        isOpen.value[index] = false;
    }
}

</script>
<template>
    <div class="flex flex-row items-center gap-10 w-full">
        <div v-for="card, index in props.cards">
            <div class="relative">
                <UButton v-if="index > 1" class="absolute right-0" :onclick="removeCard(index)" icon="i-heroicons-x-mark" size="sm" square variant="ghost" color="red" />
                <UButton color="white" @click="isOpen[index] = true">
                    <NGramCard :card="card" :remove="removeCard(index)"/>
                </UButton>
            </div>
            <UModal v-model="isOpen[index]">
                <NGramAllCards :change-card="changeCard(index)"/>
            </UModal>
        </div>

        <UButton v-if="props.cards.length < 8" :onclick="addCard" icon="i-heroicons-plus" size="sm" square variant="solid" color="yellow" />
    </div>
</template>
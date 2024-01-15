<script setup lang="ts">
import  { cr_cards, type Card } from "../../models/cards";

const card_list = ref([cr_cards[0]]);
const isOpen = ref([false, false, false, false, false, false, false, false]);

function addCard() {
    card_list.value.includes
    let index = Math.round(Math.random()*cr_cards.length)
    while(card_list.value.includes(cr_cards[index])){
        index = Math.round(Math.random()*cr_cards.length);
    }
    card_list.value.push(cr_cards[index]);
}

function removeCard(index: number){
    return () => {
        card_list.value.splice(index, 1);
    }
}

function changeCard(index: number){
    return (card: Card) => {
        card_list.value[index] = card;
        isOpen.value[index] = false;
    }
}

</script>
<template>
    <div class="flex flex-row items-center gap-10 w-full">
        <div v-for="card, index in card_list">
            <div class="relative">
                <UButton class="absolute right-0" :onclick="removeCard(index)" icon="i-heroicons-x-mark" size="sm" square variant="ghost" color="red" />
                <UButton color="white" @click="isOpen[index] = true">
                    <NGramCard :card="card" :remove="removeCard(index)"/>
                </UButton>
            </div>
            <UModal v-model="isOpen[index]">
                <NGramAllCards :change-card="changeCard(index)"/>
            </UModal>
        </div>

        <UButton v-if="card_list.length < 8" :onclick="addCard" icon="i-heroicons-plus" size="sm" square variant="solid" color="yellow" />
    </div>
</template>
<script setup lang="ts">
    import { cr_cards, type Card } from '~/models/cards';
    const props = defineProps<{changeCard: (card: Card) => void}>();
</script>
<template>
    <div class="flex flex-row flex-wrap items-center justify-center">
        <div v-for="card in cr_cards">
            <UButton :onclick="() => props.changeCard(card)" variant="ghost">
                <NGramCard :card="card" />
            </UButton>
        </div>
    </div>
</template>
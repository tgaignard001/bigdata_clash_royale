<script setup lang="ts">
    const deck_list = ["0117006d0f375b4d", "1569280b532b6245", "184a0e6850406c00", "0e032d2905605362"]
</script>

<template>
    <div class="flex flex-col gap-4 m-4">
        <div v-for="deck in deck_list" class="flex flex-row items-center">
            <NuxtLink :to="`/decks/${deck}`">
                <Deck :str-deck="deck"/>
            </NuxtLink>
        </div>
    </div>
</template>

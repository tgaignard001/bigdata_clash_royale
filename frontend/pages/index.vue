<script setup lang="ts">

const items = [{
    label: 'Home',
    key:"home"
}, {
    label: 'Decks',
    key: 'decks'
}, {
    label: 'N-grams',
    key: 'ngrams'
}]
</script>

<template>
    <div class="flex flex-row items-start gap-4 p-10 bg-[url('/ui-tex-2.png')] min-h-screen">
        <div class="flex flex-col" >
            <NuxtImg src="/bigRoyaleData.png" width="150"/>
            <NuxtImg src="/balloon.png" width="150"/>
        </div>
        <UTabs :items="items" class="w-full pt-10" >
            <template #item="{ item }">
                <div v-if="item.key === 'home'" class="flex space-y-3">
                    <Home />
                </div>
                <div v-if="item.key === 'decks'" class="space-y-3">
                    <Decks />
                </div>
                <div v-if="item.key === 'ngrams'" class="space-y-3">
                    <Ngram />
                </div>
            </template>
        </UTabs>
    </div>
</template>
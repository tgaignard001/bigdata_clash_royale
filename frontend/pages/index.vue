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
    <div class="flex flex-col items-center gap-2 p-10  bg-[url('/ui-tex-2.png')] min-h-screen">
        <div class="p-4">
            BIGROYALEDATA
        </div>
        <UTabs :items="items" class="w-full" >
            <template #item="{ item }">
                <div v-if="item.key === 'home'" class="space-y-3">
                    <Home />
                </div>
                <div v-if="item.key === 'decks'" class="space-y-3">
                    <DeckList />
                </div>
                <div v-if="item.key === 'ngrams'" class="space-y-3">
                    <Ngram />
                </div>
            </template>
        </UTabs>

    </div>
</template>
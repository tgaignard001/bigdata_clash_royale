<script setup lang="ts">

    const props = defineProps(["strDeck"]);
    const deck = new Deck(props.strDeck)

    function handleImageError(card: any) {
        card[1] = "~/public/card-champion-unknown.png";
    }
</script>
<template>
    <div class="flex flex-row gap-2 border-4 border-black p-4">
        <div class="flex flex-row content-center items-center ">
            <div v-for="card in deck.cards()" class="flex flex-col items-center">
                {{ card[0] }}
                <img :src="card[1]" onerror="this.src='/card-champion-unknown.png'"/>
            </div>
        </div>
        <table class="table-auto border border-collapse w-full">
            <thead>
                <tr>
                    <th class="border">Victories</th>
                    <th class="border">Games</th>
                    <th class="border">Players</th>
                    <th class="border">Clan</th>
                    <th class="border">Mean force</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td class="border text-center">0</td>
                    <td class="border text-center">0</td>
                    <td class="border text-center">0</td>
                    <td class="border text-center">0</td>
                    <td class="border text-center">0</td>
                </tr>
            </tbody>
        </table>
    </div>

</template>
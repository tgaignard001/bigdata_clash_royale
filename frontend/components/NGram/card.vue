<script setup lang="ts">
    import type { Card } from '~/models/cards';

    const props = defineProps<{card: Card }>();

</script>
<template>
    <div class="bg-white rounded-xl pl-2 pr-2">
        <div class="flex flex-col items-center">
            <NuxtImg :src="props.card.iconUrls.medium" width="100"/>
            <p>{{ props.card.name }}</p>
        </div>
    </div>
</template>
<script setup lang="ts">
const props = defineProps<{ title: string, body: string, iconPath: string }>();
</script>

<template>
    <div class="w-full h-full flex flex-col justify-center items-center gap-8 p-4 text-center">
        <NuxtImg :src="props.iconPath" width="70" />
        <div class="font-sans">
            {{ props.title }}
        </div>
        <div class="font-bold text-xl">
            {{ props.body }}
        </div>
    </div>
</template>
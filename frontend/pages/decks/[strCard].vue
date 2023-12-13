<script setup lang="ts">
    const route = useRoute();
</script>
<template>
    Page du deck {{ route.params.strCard }}
    <Deck :str-deck="route.params.strCard" />
</template>
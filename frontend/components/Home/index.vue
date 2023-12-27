<script setup>
</script>

<template>
    <div class="flex flex-col h-[81vh] w-full gap-20 p-20">
        <div class="flex flex-row flex-1 gap-20">
            <HomeContainer>
                <HomeAbout />
            </HomeContainer>
            <HomeContainer>
                <HomeStats />
            </HomeContainer>
        </div>
        <div class="flex flex-row flex-1 gap-20">
            <HomeContainer>
                <HomeBetterDeck />
            </HomeContainer>
        </div>
    </div>
</template>
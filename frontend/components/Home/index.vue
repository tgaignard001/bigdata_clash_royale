<script setup>
</script>

<template>
    <div class="flex flex-col h-[83vh] w-full gap-20 p-20">
        <div class="flex flex-row gap-20">
            <HomeContainer class="flex-auto">
                <HomeAbout />
            </HomeContainer>
            <HomeContainer>
                <HomeStats title="Taille du TopK" body="500" icon-path="/tower.png"/>
            </HomeContainer>
            <HomeContainer>
                <HomeStats title="Nombre de parties minimum" body="100" icon-path="/diamond.png"/>
            </HomeContainer>
            <HomeContainer>
                <HomeStats title="Nombre de joueurs uniques minimum" body="10" icon-path="/diamond.png"/>
            </HomeContainer>
        </div>
        <div class="flex flex-row flex-1 gap-20">
            <HomeContainer>
                <HomeBetterDeck />
            </HomeContainer>
        </div>
    </div>
</template>
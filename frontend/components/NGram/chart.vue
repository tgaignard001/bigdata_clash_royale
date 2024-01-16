<script setup lang="ts">
import { Line } from 'vue-chartjs'
import { Chart as ChartJS, Title, Tooltip, Legend, PointElement, LineElement, CategoryScale, LinearScale } from 'chart.js'

ChartJS.register(Title, Tooltip, Legend, PointElement, LineElement, CategoryScale, LinearScale)

const chartData = {
    labels: [ 'January', 'February', 'March' , 'April', 'May', 'June', 'July', 'August', 'September', 'October'],
    datasets: [ { data: [40, 20, 12, 100, 20, 50, 70, 45, 50, 80] } ]
}

const chartOptions = {
    responsive: true,
}


</script>
<template>
    <div class="flex flex-col items-center w-full h-full">
        <Line class="rounded-xl bg-white" :options="chartOptions" :data="chartData" />
    </div>
</template>
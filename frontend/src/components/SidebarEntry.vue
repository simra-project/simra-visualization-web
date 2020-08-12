<template>
    <div class="entry">
        <div class="entry-label" @click="$emit('entryClicked')">
            <div class="entry-icon">
                <i class="fas fa-lg" :class="icon" :style="'color: ' + iconColor "></i>
            </div>
            <div class="entry-text">{{ title }}</div>
        </div>

        <div class="entry-content">
            <slot/>
        </div>
    </div>
</template>

<script>
export default {
    name: "SidebarEntry",
    props: [
        'title',
        'icon',
        'iconColor',
    ]
};
</script>

<style scoped lang="scss">
    .entry {
        position: relative;

        .entry-label {
            display: flex;
            align-items: center;
            height: 52px;
            padding: 12px 8px;

            &:hover, &:active {
                background-color: #eaeaea;//#dcdcdc;
                cursor: pointer;
            }

            .entry-icon {
                width: 60px;
                flex-shrink: 0;
                padding-right: 6px;
                text-align: center;
            }

            .entry-text {
                width: 100%;
                color: black;
                font-size: 18px;
                font-family: "Segoe UI VSS (Regular)", "Segoe UI", "-apple-system", BlinkMacSystemFont, Roboto, "Helvetica Neue", Helvetica, Ubuntu, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
                user-select: none;
            }
        }

        .entry-content {
            display: none;
            padding: 10px 20px;
            background-color: #eaeaea;
            border-bottom: 1px solid #e0e0e0;
        }

        &.selected {
            .entry-label {
                font-weight: bold;
                background-color: #dcdcdc !important;
                border-bottom: 1px solid #e0e0e0;

                &::before {
                    bottom: 0;
                    background-color: rgba(0, 0, 0, .2);
                    content: "";
                    left: 0;
                    position: absolute;
                    top: 0;
                    width: 3px;
                }
            }

            .entry-content {
                display: block;
            }
        }
    }
</style>

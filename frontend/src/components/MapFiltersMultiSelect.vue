<template>
    <b-dropdown v-model="value" multiple @change="$emit('input', value)">
        <button class="button is-primary is-outlined custom-multi-select" type="button" slot="trigger">
            <span v-if="value.length === 0 || value.length === options.length" class="no-selection">
                {{ emptyLabel }}
            </span>
            <span v-else>
                {{ optionNameForId(value[0]) }}<template v-if="value.length > 1"> + {{ value.length - 1 }}</template>
            </span>
        </button>

        <b-dropdown-item v-for="option in options" :value="option.id">
            <i class="far fa-circle"></i>
            <i class="far fa-check-circle"></i>
            <span>{{ option.name }}</span>
        </b-dropdown-item>
    </b-dropdown>
</template>

<script>
export default {
    name: "MapFiltersMultiSelect",
    props: {
        value: Array, // -> from v-model
        options: Array,
        emptyLabel: String,
    },
    methods: {
        optionNameForId(optionId) {
            return this.options.filter(x => x.id === optionId).pop().name;
        }
    }
};
</script>

<style lang="scss">
    button.custom-multi-select {
        border-color: #dbdbdb !important;
        height: 2.24em;
        padding-right: 40px;
        background-color: white !important;

        &:hover, &:focus, &:active {
            border-color: #b5b5b5 !important;
            background-color: white !important;
        }

        &:focus, &:active {
            border-color: #3273dc !important;
            box-shadow: 0 0 0 0.125em rgba(50, 115, 220, 0.25) !important;
        }

        span {
            color: #363636;

            &.no-selection {
                color: rgba(122, 122, 122, 0.7);
            }
        }

        &::after {
            border-radius: 2px;
            border: 3px solid transparent;
            border-top: 0;
            border-right: 0;
            content: " ";
            display: block;
            height: 0.625em;
            margin-top: -0.4375em;
            pointer-events: none;
            position: absolute;
            top: 50%;
            transform: rotate(-45deg);
            transform-origin: center;
            width: 0.625em;
            border-color: #3273dc;
            right: 1.125em;
            z-index: 4;
        }
    }

    .dropdown.is-active button.custom-multi-select {
        border-color: #3273dc !important;
        box-shadow: 0 0 0 0.125em rgba(50, 115, 220, 0.25) !important;
    }

    .dropdown .dropdown-menu .dropdown-content {
        padding-bottom: 0.25rem;
        padding-top: 0.25rem;
        background-color: whitesmoke;
        box-shadow: 0 2px 12px 5px rgba(10, 10, 10, 0.1), 0 0 0 1px rgba(10, 10, 10, 0.1);

        a.dropdown-item {
            line-height: 1;
            font-size: 16px;
            color: #191919;
            background-color: transparent;
            padding-left: 12px;
            padding-right: 12px;

            &:hover {
                background-color: #3273DC;
                color: white;
            }

            &:not(.is-active) i.fa-check-circle {
                display: none;
            }

            &.is-active i.fa-circle {
                display: none;
            }

            span {
                margin-left: 10px;
            }
        }
    }
</style>

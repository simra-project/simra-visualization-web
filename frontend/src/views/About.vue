<template>
    <div class="modal-card" style="width: auto">
        <header class="modal-card-head">
            <p class="modal-card-title">{{ $t('about.title') }}</p>
        </header>
        <section class="modal-card-body">
            <div class="content" style="text-align: justify">
                <h3>SimRa: Sicherheit im Radverkehr</h3>
                Im Projekt SimRa sammeln wir – auf datenschutzkompatible Art und Weise – Daten darüber, wo es in der
                Stadt für Radfahrende zu Gefahrenhäufungen kommt, welcher Art diese sind, ob diese zeitlich oder lokal
                gehäuft auftreten und wo sich die Hauptverkehrsflüsse auf dem Rad bewegen. Hierfür wurde im Projekt eine
                Smartphone-App entwickelt, die mittels GPS-Daten Fahrtrouten aufzeichnet und dabei
                Beschleunigungssensoren zum Detektieren von Gefahrensituationen nutzt – bspw. plötzliches Bremsen,
                Ausweichen oder gar einen Sturz. Im Anschluss an die Fahrt werden Radfahrende gebeten, diese
                detektierten Gefahrensituationen zu kategorisieren und zu annotieren, etwaige nicht detektierte
                Gefahrensituationen zu ergänzen und einen Upload auf die Projektserver frei zu geben.
                <br><br>
                Aktuell ist die App verfügbar für Android (6.0+) und IOS. <br>

                <a href="https://itunes.apple.com/de/app/simra/id1459516968?mt=8" class="button">
                <i class="fab fa-app-store" style="margin-right: 6px;"></i> Apple App Store
                </a>&nbsp;&nbsp;
                <a href="https://play.google.com/store/apps/details?id=de.tuberlin.mcc.simra.app" class="button">
                    <i class="fab fa-google-play" style="margin-right: 6px;"></i> Google Playstore
                </a>

                <h3>Entwicklung</h3>
                Dieses Projekt wurde durch Adrian Gronau (Link), Konstantin Köhler (Link) und Richard Viehöver (Link)
                entwickelt. Ursprünglich wurde diese Webseite im Rahmen eines Studentenprojektes im Wintersemester
                2019/2020 an der Technischen Universität Berlin mit grundlegenden Funktionen entwickelt. <br> Jedoch
                haben erst Fördermittel der Gemeinden Zeuthen, Eichwalde und Schulzendorf die Erweiterung und
                Entwicklung vieler neuer Funktionen ermöglicht.<br>
                Das Projekt wurde im Rahmen einer Bachelorarbeit durch Sebastian Führ 
                (<a href="https://www.linkedin.com/in/sebastian-f%C3%BChr-578998180/">Link</a>) um Funktionalitäten 
                zur Bestimmung der Beliebtheit von Straßensegmenten erweitert.
            </div>
        </section>
        <footer class="modal-card-foot">
            <button class="button is-primary" type="button" @click="$parent.close()">{{ $t('about.close') }}</button>
        </footer>
    </div>
</template>

<script>
export default {
    name: "about",
    components: {},
};
</script>

<style lang="scss">
    footer.modal-card-foot {
        justify-content: center;
    }

    .modal-background {
        background-color: rgba(10, 10, 10, 0.75);
    }
</style>

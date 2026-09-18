package app;

import collection.CollectionRecord;
import drive.MobileDriveTemplate;
import drive.Schedule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import modality.DonationModality;
import modality.ModalityFactory;
import modality.PlasmaApheresisFactory;
import modality.PlateletApheresisFactory;
import modality.WholeBloodFactory;
import service.AptitudeEvaluation;
import service.FefoAlertItem;
import service.InventoryService;
import service.ModalitySummary;

public class Main {

        public static void main(String[] args) {
                System.out.println("=== HEMOCENTRO REGIONAL DEL NORORIENTE ===");

                // ---------------------------------------------------------------------
                // 1. PROTOTYPE PATTERN DEMONSTRATION
                // ---------------------------------------------------------------------
                List<String> baseSupplies = List.of(
                                "Camillas de extraccion",
                                "Neveras portatiles de transporte",
                                "Tensiometros y fonendoscopios",
                                "Kits de bioseguridad y flebotomia");

                Schedule baseSchedule = new Schedule("08:00", "16:00");
                MobileDriveTemplate baseTemplate = new MobileDriveTemplate(
                                "Jornada universitaria estandar",
                                "Sede Principal",
                                "Bucaramanga",
                                100,
                                DonationModality.SANGRE_TOTAL,
                                baseSchedule,
                                baseSupplies);

                // Generate clones
                MobileDriveTemplate clone1 = baseTemplate.clone();
                clone1.setName("UIS - Bucaramanga");
                clone1.setTargetUnits(80);

                MobileDriveTemplate clone2 = baseTemplate.clone();
                clone2.setName("UFPS - Cucuta");
                clone2.setMunicipality("Cucuta");
                clone2.setTargetUnits(120);
                clone2.addSupply("Carpa termica"); // Extra supply for Cucuta

                System.out.println("Plantilla base: " + baseTemplate.getName() + " | insumos: "
                                + baseTemplate.getSupplyCount());
                System.out.println("  Jornada clonada 1: " + clone1.getName() + " | meta: " + clone1.getTargetUnits()
                                + " | insumos: " + clone1.getSupplyCount());
                System.out.println("  Jornada clonada 2: " + clone2.getName() + " | meta: " + clone2.getTargetUnits()
                                + " | insumos: " + clone2.getSupplyCount());
                System.out.println();
                System.out.println("  Verificacion plantilla base -> insumos: " + baseTemplate.getSupplyCount()
                                + " (NO fue alterada)");
                System.out.println();

                // ---------------------------------------------------------------------
                // 2. ABSTRACT FACTORY & BUILDER PATTERNS DEMONSTRATION
                // ---------------------------------------------------------------------
                ModalityFactory wholeBloodFactory = new WholeBloodFactory();
                ModalityFactory plateletFactory = new PlateletApheresisFactory();
                ModalityFactory plasmaFactory = new PlasmaApheresisFactory();

                List<CollectionRecord> registeredCollections = new ArrayList<>();

                // Unit 1: Whole Blood - Normal / APTA
                CollectionRecord unit1 = CollectionRecord.builder()
                                .consecutive(1)
                                .donorId("CC-1098765432")
                                .siteCode("BUC")
                                .collectionDate(LocalDate.of(2026, 3, 20))
                                .modalityFactory(wholeBloodFactory)
                                .actualVolumeMl(455.0)
                                .punctureTimeMinutes(9.0)
                                .phlebotomist("Enf. Carlos Perez")
                                .supplyBatch("LOT-2026-03-A")
                                .campaign("Jornada UIS")
                                .build();
                registeredCollections.add(unit1);

                // Unit 2: Platelet Apheresis - APTA (expires 2026-04-18, expired by cutoff
                // 2026-04-20)
                CollectionRecord unit2 = CollectionRecord.builder()
                                .consecutive(2)
                                .donorId("CC-1098765433")
                                .siteCode("BUC")
                                .collectionDate(LocalDate.of(2026, 4, 13))
                                .modalityFactory(plateletFactory)
                                .actualVolumeMl(298.0)
                                .punctureTimeMinutes(60.0)
                                .phlebotomist("Enf. Maria Rodriguez")
                                .supplyBatch("LOT-2026-04-P")
                                .campaign("Jornada Fija BUC")
                                .observations("Procedimiento de aferesis plaquetaria estandar")
                                .build();
                registeredCollections.add(unit2);

                // Unit 3: Whole Blood - Out of range (390 mL < 405 mL minimum) -> NO APTA
                CollectionRecord unit3 = CollectionRecord.builder()
                                .consecutive(3)
                                .donorId("CC-1098765434")
                                .siteCode("CUC")
                                .collectionDate(LocalDate.of(2026, 3, 22))
                                .modalityFactory(wholeBloodFactory)
                                .actualVolumeMl(390.0) // Nominal 450 mL -> min 405 mL
                                .punctureTimeMinutes(10.0)
                                .phlebotomist("Enf. Laura Gomez")
                                .supplyBatch("LOT-2026-03-B")
                                .campaign("Jornada UFPS")
                                .observations("Flujo lento durante extraccion")
                                .build();
                registeredCollections.add(unit3);

                // Unit 4: Whole Blood - Normal / APTA
                CollectionRecord unit4 = CollectionRecord.builder()
                                .consecutive(4)
                                .donorId("CC-1098765435")
                                .siteCode("BUC")
                                .collectionDate(LocalDate.of(2026, 4, 5))
                                .modalityFactory(wholeBloodFactory)
                                .actualVolumeMl(450.0)
                                .punctureTimeMinutes(9.5)
                                .phlebotomist("Enf. Carlos Perez")
                                .supplyBatch("LOT-2026-04-A")
                                .campaign("Jornada UIS")
                                .build();
                registeredCollections.add(unit4);

                // Unit 5: Platelet Apheresis - APTA (expires 2026-04-23), contains adverse
                // event handled by phlebotomist
                CollectionRecord unit5 = CollectionRecord.builder()
                                .consecutive(5)
                                .donorId("CC-1098765436")
                                .siteCode("CUC")
                                .collectionDate(LocalDate.of(2026, 4, 18))
                                .modalityFactory(plateletFactory)
                                .actualVolumeMl(303.0)
                                .punctureTimeMinutes(64.0)
                                .phlebotomist("Enf. Laura Gomez")
                                .supplyBatch("LOT-2026-04-P2")
                                .campaign("Jornada UFPS")
                                .addAdverseEvent(
                                                "Reaccion vasovagal leve al minuto 50; donante hidratado y estabilizado")
                                .observations("Procedimiento culminado satisfactoriamente tras pausa de 5 min")
                                .build();
                registeredCollections.add(unit5);

                // Unit 6: Plasma Apheresis - APTA (expires 2027-04-10)
                CollectionRecord unit6 = CollectionRecord.builder()
                                .consecutive(6)
                                .donorId("CC-1098765437")
                                .siteCode("BUC")
                                .collectionDate(LocalDate.of(2026, 4, 10))
                                .modalityFactory(plasmaFactory)
                                .actualVolumeMl(612.0)
                                .punctureTimeMinutes(45.0)
                                .phlebotomist("Enf. Maria Rodriguez")
                                .supplyBatch("LOT-2026-04-PLS")
                                .campaign("Sede Central")
                                .observations("Procedimiento de plasmaferesis completado segun protocolo")
                                .firstTimeDonor(true)
                                .build();
                registeredCollections.add(unit6);

                // ---------------------------------------------------------------------
                // 3. DISPLAY REGISTERED UNITS & FITNESS EVALUATIONS
                // ---------------------------------------------------------------------
                InventoryService inventoryService = new InventoryService();

                System.out.println("--- UNIDADES REGISTRADAS ---");
                for (CollectionRecord record : registeredCollections) {
                        AptitudeEvaluation evaluation = inventoryService.evaluateFitness(record);
                        if (evaluation.isFit()) {
                                System.out.printf("%s | %-18s | %3.0f mL | %-7s | vence %s%n",
                                                record.getUnitCode(),
                                                record.getModality(),
                                                record.getActualVolumeMl(),
                                                evaluation.getStatus().getLabel(),
                                                evaluation.getExpirationDate());
                        } else {
                                System.out.printf("%s | %-18s | %3.0f mL | %-7s | %s%n",
                                                record.getUnitCode(),
                                                record.getModality(),
                                                record.getActualVolumeMl(),
                                                evaluation.getStatus().getLabel(),
                                                evaluation.getRejectionReason());
                        }
                }
                System.out.println();

                // ---------------------------------------------------------------------
                // 4. CONTROLLED BUILDER EXCEPTION DEMONSTRATION
                // ---------------------------------------------------------------------
                try {
                        // Attempting to build a collection record without supplyBatch (loteInsumos)
                        CollectionRecord.builder()
                                        .consecutive(7)
                                        .donorId("CC-1098765499")
                                        .siteCode("BUC")
                                        .collectionDate(LocalDate.of(2026, 4, 20))
                                        .modalityFactory(wholeBloodFactory)
                                        .actualVolumeMl(450.0)
                                        .punctureTimeMinutes(11.0)
                                        .phlebotomist("Enf. Carlos Perez")
                                        // supplyBatch is intentionally omitted
                                        .build();
                } catch (IllegalStateException e) {
                        System.out.println("[ERROR CONTROLADO] No se puede construir el registro: falta 'loteInsumos' ("
                                        + e.getMessage() + ")");
                }
                System.out.println();

                // ---------------------------------------------------------------------
                // 5. CONSOLIDATED INVENTORY REPORT
                // ---------------------------------------------------------------------
                System.out.println("--- CONSOLIDADO POR MODALIDAD ---");
                System.out.printf("%-21s %5s %8s %15s %12s %16s%n",
                                "Modalidad", "Aptas", "NoAptas", "Vol.util(mL)", "Aprovech.", "T.puncion prom");

                List<ModalitySummary> summaries = inventoryService.generateModalityConsolidated(registeredCollections);
                for (ModalitySummary summary : summaries) {
                        System.out.printf("%-21s %5d %8d %15.0f %10.1f %% %12.1f min%n",
                                        summary.getModality(),
                                        summary.getFitCount(),
                                        summary.getUnfitCount(),
                                        summary.getTotalUsableVolumeMl(),
                                        summary.getUtilizationPercentage(),
                                        summary.getAveragePunctureTimeMinutes());
                }
                System.out.println();

                // ---------------------------------------------------------------------
                // 6. FEFO ALERT (CUTOFF: 2026-04-20, EXPIRES <= 7 DAYS)
                // ---------------------------------------------------------------------
                LocalDate cutoffDate = LocalDate.of(2026, 4, 20);
                System.out.printf("--- ALERTA FEFO (corte %s, vencen en <= 7 dias) ---%n", cutoffDate);

                List<FefoAlertItem> alerts = inventoryService.generateFefoAlert(registeredCollections, cutoffDate);
                int index = 1;
                for (FefoAlertItem alert : alerts) {
                        System.out.printf("%d. %s%n", index++, alert);
                }
        }
}

# Caso 1 — Fraccionamiento y Trazabilidad de Componentes Sanguíneos

## Descripción del Proyecto

Este proyecto implementa un prototipo funcional para el **Hemocentro Regional del Nororiente**, una aplicación de consola en Java (SE 17+) que resuelve dos problemas críticos de calidad:

1. **Mezcla de elementos entre modalidades distintas** (ej.: etiquetar plaquetas con protocolo de sangre total).
2. **Registros incompletos o inconsistentes** que causan vacíos de trazabilidad.

El sistema garantiza que cada colecta de sangre obtenga automáticamente la bolsa, etiqueta y protocolo de conservación coherentes entre sí según su modalidad, y que ningún registro pueda existir en estado incompleto.

---

## Patrones de Diseño Utilizados

### 1. Abstract Factory — Familias de elementos por modalidad

**Problema que resuelve:**  
En el hemocentro se presentaron incidentes graves porque se mezclaron elementos de distintas modalidades (ej.: etiquetar plaquetas con temperatura de sangre total 2–6 °C en vez de 20–24 °C en agitación). Esto causó el descarte de 18 unidades.

**Cómo lo resuelve:**  
Se define una interfaz `ModalityFactory` con tres métodos: `createBag()`, `createLabel()` y `createPreservationProtocol()`. Cada modalidad tiene su propia fábrica concreta (`WholeBloodFactory`, `PlateletApheresisFactory`, `PlasmaApheresisFactory`) que produce **exclusivamente** los tres productos compatibles entre sí. El código cliente nunca usa `if` ni `switch` para decidir temperatura, prefijo o vigencia; simplemente recibe una fábrica y trabaja contra las interfaces de producto.

**Clases involucradas (paquete `modalidad`):**
- `ModalityFactory` — interfaz de la fábrica abstracta.
- `CollectionBag`, `TraceabilityLabel`, `PreservationProtocol` — interfaces de producto.
- `WholeBloodFactory` — fábrica concreta para SANGRE_TOTAL (CPD-A1, bolsa cuádruple, E00, 2–6 °C, 35 días).
- `PlateletApheresisFactory` — fábrica concreta para AFERESIS_PLAQUETAS (ACD-A, kit PLT, E30, 20–24 °C agitación, 5 días).
- `PlasmaApheresisFactory` — fábrica concreta para AFERESIS_PLASMA (ACD-A, kit PLS, E70, ≤ −25 °C, 365 días).
- `DonationModality` — enumeración con las tres modalidades.

**¿Por qué Abstract Factory y no simplemente herencia?**  
Porque el problema no es crear un solo objeto, sino una *familia* de objetos que deben ser coherentes entre sí. Abstract Factory garantiza a nivel de diseño (no en tiempo de ejecución) que nunca se combine una bolsa de sangre total con una etiqueta de plaquetas.

---

### 2. Builder — Construcción del registro de colecta

**Problema que resuelve:**  
Se registró una colecta sin el consecutivo del lote de insumos, generando un vacío de trazabilidad sancionado por la autoridad sanitaria. Un registro incompleto o inconsistente es inaceptable.

**Cómo lo resuelve:**  
La clase `CollectionRecord` es **inmutable** (todos los campos son `final`, sin setters). Solo puede construirse a través de un `Builder` encadenado que valida *todas* las reglas de negocio antes de entregar el objeto:

- **Campos obligatorios:** consecutivo, documento del donante, código de sede, fecha de colecta, fábrica de modalidad, volumen real, tiempo de punción, flebotomista, lote de insumos.
- **Campos opcionales:** campaña, observaciones, eventos adversos, indicador de primera vez.
- **Validaciones en `build()`:**
  - Lanza `IllegalStateException` si falta cualquier campo obligatorio.
  - Lanza `IllegalStateException` si el tiempo de punción supera 15 minutos sin observación registrada.
  - Lanza `IllegalStateException` si hay eventos adversos y no se indicó flebotomista responsable.

**Clases involucradas (paquete `colecta`):**
- `CollectionRecord` — entidad inmutable con clase interna `Builder`.

**¿Por qué Builder y no un constructor con muchos parámetros?**  
Un constructor con 13+ parámetros sería propenso a errores (intercambiar parámetros del mismo tipo). Builder permite construcción progresiva, legible y con validación centralizada en un solo punto (`build()`), haciendo imposible que exista un registro en estado inválido.

---

### 3. Prototype — Plantillas de jornada móvil

**Problema que resuelve:**  
El coordinador de promoción necesita crear jornadas móviles a partir de plantillas reutilizables. Modificar una jornada creada a partir de una plantilla **nunca** debe alterar la plantilla original ni otras jornadas derivadas de ella.

**Cómo lo resuelve:**  
`MobileDriveTemplate` implementa `Cloneable` con **clonación profunda (deep copy)**: tanto el objeto `Schedule` (horario) como la lista de insumos (`requiredSupplies`) se duplican en instancias completamente nuevas. Así, agregar un insumo a la jornada clonada de Cúcuta no afecta a la plantilla base ni a la jornada de Bucaramanga.

**Clases involucradas (paquete `jornada`):**
- `MobileDriveTemplate` — plantilla clonable con deep copy.
- `Schedule` — objeto de horario que también implementa `Cloneable`.

**¿Por qué Prototype y no simplemente crear objetos nuevos?**  
Porque la lógica de negocio requiere partir de configuraciones predefinidas complejas (sede, municipio, meta, modalidad, horario, lista de insumos) y solo ajustar lo que cambia. Prototype evita re-crear toda esa configuración desde cero y garantiza independencia total entre copias gracias a la clonación profunda.

---

## Componente Algorítmico — ServicioInventario

La clase `InventoryService` (paquete `servicio`) implementa lógica pura sin patrones de diseño:

| Funcionalidad | Descripción |
|---|---|
| **Vencimiento** | `fecha de colecta + días de vigencia de la modalidad` usando `LocalDate` |
| **Aptitud** | Volumen real dentro del ±10% del nominal → `APTA`; fuera → `NO_APTA (VOLUMEN_FUERA_DE_RANGO)` |
| **Consolidado** | Por modalidad: unidades aptas, no aptas, volumen útil total, porcentaje de aprovechamiento (1 decimal) |
| **Alerta FEFO** | Unidades aptas que vencen en ≤ 7 días desde la fecha de corte, ordenadas por vencimiento ascendente y consecutivo ascendente |
| **Eficiencia de punción** | Promedio del tiempo de punción por modalidad, en minutos con 1 decimal |

---

## Estructura de Paquetes

```
src/
├── modalidad/           ← Abstract Factory (interfaces y fábricas)
│   ├── DonationModality.java
│   ├── CollectionBag.java
│   ├── TraceabilityLabel.java
│   ├── PreservationProtocol.java
│   ├── ModalityFactory.java
│   ├── WholeBloodFactory.java
│   ├── PlateletApheresisFactory.java
│   └── PlasmaApheresisFactory.java
├── colecta/             ← Builder (registro inmutable)
│   └── CollectionRecord.java
├── jornada/             ← Prototype (plantillas clonables)
│   ├── MobileDriveTemplate.java
│   └── Schedule.java
├── servicio/            ← Lógica algorítmica pura
│   ├── AptitudeStatus.java
│   ├── AptitudeEvaluation.java
│   ├── ModalitySummary.java
│   ├── FefoAlertItem.java
│   └── InventoryService.java
└── app/                 ← Programa principal de demostración
    └── Main.java
```

---

## Cómo Compilar y Ejecutar

### Requisitos
- **Java SE 17+** (probado con OpenJDK 25)
- No requiere frameworks, Lombok ni librerías externas

### Compilación
```bash
javac -encoding UTF-8 -d bin src/modalidad/*.java src/colecta/*.java src/jornada/*.java src/servicio/*.java src/app/*.java
```

### Ejecución
```bash
java -cp bin app.Main
```

### Salida esperada
```
=== HEMOCENTRO REGIONAL DEL NORORIENTE ===
Plantilla base: Jornada universitaria estandar | insumos: 4
  Jornada clonada 1: UIS - Bucaramanga | meta: 80 | insumos: 4
  Jornada clonada 2: UFPS - Cucuta | meta: 120 | insumos: 5

  Verificacion plantilla base -> insumos: 4 (NO fue alterada)

--- UNIDADES REGISTRADAS ---
E00-BUC-26-00001 | SANGRE_TOTAL       | 455 mL | APTA    | vence 2026-04-24
E30-BUC-26-00002 | AFERESIS_PLAQUETAS | 298 mL | APTA    | vence 2026-04-18
E00-CUC-26-00003 | SANGRE_TOTAL       | 390 mL | NO APTA | VOLUMEN_FUERA_DE_RANGO
E00-BUC-26-00004 | SANGRE_TOTAL       | 450 mL | APTA    | vence 2026-05-10
E30-CUC-26-00005 | AFERESIS_PLAQUETAS | 303 mL | APTA    | vence 2026-04-23
E70-BUC-26-00006 | AFERESIS_PLASMA    | 612 mL | APTA    | vence 2027-04-10

[ERROR CONTROLADO] No se puede construir el registro: falta 'loteInsumos'

--- CONSOLIDADO POR MODALIDAD ---
Modalidad             Aptas  NoAptas    Vol.util(mL)    Aprovech.   T.puncion prom
SANGRE_TOTAL              2        1             905       66.7 %          9.5 min
AFERESIS_PLAQUETAS        2        0             601      100.0 %         62.0 min
AFERESIS_PLASMA           1        0             612      100.0 %         45.0 min

--- ALERTA FEFO (corte 2026-04-20, vencen en <= 7 dias) ---
1. E30-BUC-26-00002 | AFERESIS_PLAQUETAS | vence 2026-04-18 | quedan -2 dias (VENCIDA)
2. E30-CUC-26-00005 | AFERESIS_PLAQUETAS | vence 2026-04-23 | quedan 3 dias
3. E00-BUC-26-00001 | SANGRE_TOTAL | vence 2026-04-24 | quedan 4 dias
```

---

## Autor
Proyecto desarrollado como Caso de Estudio 1 para la materia **Patrones de Software**, Cuarto Semestre de Ingeniería de Software.

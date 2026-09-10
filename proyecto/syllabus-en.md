# Curriculum — Weekly Language Challenge System

**Edition 2.4 — September 2026**
Auditable document. Single source of the application's pedagogical content.

Four roles appear throughout: the **user** (who practises), the **designer** (who sets skills, levels and anchors), the **reviewer** (native speaker or teacher who validates) and the **developer** (who implements the application). No personal names are used.

---

## 0. How to read this document

This document serves the same function as a university course syllabus: it states what is taught, at what level, on what basis that level was assigned, and with what vocabulary.

It is written for four readers:

| Reader | What to look at |
|---|---|
| **The user** | Sections 3, 4, 5 and 6: what is practised and in what order |
| **The reviewer** (native speaker or teacher) | Sections 1, 4, 5 and 7: whether the level assignments and vocabulary are correct |
| **The developer** | Sections 2 and 3: data structure, challenge types and generation rules |
| **The contributor** | Sections 2 and 8: how the content is structured and under what licence |

**How it is generated.** This document and the JSON consumed by the application are compiled from the same Markdown source file. They are not drafted separately, so they cannot diverge.

**What it is not.** It is not a course. It does not replace lessons or a teaching method. It is a system of deliberate practice, aimed at users who have already studied the theory and need to force themselves into active use.

---

## 1. Reference sources and level-assignment method

The question this document must be able to answer is: *why is "Hedging" at B2 and not C1?* If the answer were an undeclared judgement of the designer, the document would not be auditable. Every level assignment is anchored to a public source.

### 1.1 English

**English Grammar Profile (EGP)**, from Cambridge's English Profile programme. This is the primary reference. Its logic is not prescriptive but empirical: it identifies *criterial features* — structures that distinguish one level from the previous one because they appear correctly used from that point and not before.

Relevant details of the EGP method:

- It was built on the **Cambridge Learner Corpus**, over 55 million words of text produced by real exam candidates, with more than 140 first languages represented.
- It contains more than **1,200 descriptors** of grammatical structures, each mapped to the CEFR level at which it is considered acquired.
- The acquisition criterion includes a correct-use threshold, raised to 60% accuracy to offset first-language effects.
- Descriptors come in two kinds: **form-based** (grammatical) and **use-based** (pragmatic). This distinction matters here: it justifies assigning a level to a skill such as "Hedging" even though it is not a grammatical structure.

An EGP example: indirect questions (*can you tell me…?*) are a criterial feature of B1, because an A2 learner typically does not produce them correctly and a B1 learner does.

**English Vocabulary Profile (EVP)**, from the same programme, for the level of lexical items.

### 1.2 German

**Profile deutsch** (Glaboniat, Müller, Rusch et al., Langenscheidt, version 2.0, 2005), the official CEFR implementation for German as a foreign language. It is the reference cited by the Goethe-Institut's own exam-objective handbooks.

**A methodological finding that changes the design of the German bank:**

Profile deutsch describes German grammar only up to B2. The stated reason is that beyond B2 the linguistic means are no longer determinable: there are too many possible situations, varieties and specialised lexicons depending on each user's domain. The first edition of Profile deutsch stopped at B2 for that reason; the second integrated a dictionary to cover C1 and C2.

The work's explicit assumption is that **the learner knows every grammatical phenomenon by the time they reach B2**, and that C1 and C2 concern the *active use* of those means, not new structures.

Direct consequence for this bank: **German C1 and C2 carry no new grammar.** They carry register, pragmatics, stylistics, lexical precision and variation. Any German C1 bank offering "new advanced structures" is inventing them. This also reduces the risk noted earlier regarding unvalidatable C1/C2 content: there is less to invent than it appeared.

**Goethe-Zertifikat, Prüfungsziele / Testbeschreibung handbooks** (A1 to C2), for objective inventories and thematic vocabulary.

**Findings verified at first hand (September 2026):**

1. The free **Goethe B2** handbook does not contain the inventory of structures: it refers explicitly to the Profile deutsch CD-ROM. The **C1** handbook goes further and states that **no vocabulary or grammar inventories exist for that level**, because authentic texts are used and the learner is expected to infer meaning through word formation.
2. The **Cambridge B2 First, C1 Advanced and C2 Proficiency** handbooks contain **no vocabulary list**: zero mentions across 264 pages. Only the B1 Preliminary handbook has one.
3. **Two independent bodies, in two languages, reach the same conclusion.** The absence of an inventory above B1 ceases to be a limitation of these sources and becomes a fact about how advanced proficiency works. It reinforces the decision that at C1/C2 the skill should be word formation and strategy, not lists.
4. The copy of **Profile deutsch** obtained is the accompanying handbook, not the CD-ROM: it contains the index of the systematic grammar but **without level labels**. It cannot close the debt on the German C1/C2 assignments.

### 1.3 Both languages

**Common European Framework of Reference (CEFR)**, and in particular the *Companion Volume*, for functional descriptors: fluency skills (giving opinions, disagreeing, negotiating, mediating) are anchored to CEFR scales, not to grammar.

**Graded reading corpus** (20 English stories written at three levels), as receptive cross-confirmation. See `AUDITORIA-LECTURA.md`: it confirmed six assignments and corrected two.

### 1.4 How to read the "Anchor" column

- `EGP` — structure recorded in the English Grammar Profile at that level.
- `EGP-use` — pragmatic-type EGP descriptor.
- `CV` — Companion Volume functional scale, not a grammatical structure.
- `CEFR` — Framework scale, not a grammatical structure.
- `PD` — Profile deutsch.
- `PD>B2` — not covered by Profile deutsch by design (German C1/C2): assigned by the designer's own judgement, **explicitly flagged as a validation debt**.
- `Aspekte` / `Netzwerk` — course-book scope-and-sequence (Klett), publicly available. See `AUDITORIA-ALEMAN.md`.
- `Goethe` — Goethe-Institut exam-objective inventory.

### 1.5 Honesty about the limits of this anchoring

Three caveats a reviewer should keep in mind:

1. **The EGP and Profile deutsch are restricted-access or paid works.** The assignments in this document rest on their public methodology and on derived literature, not on a line-by-line verified copy of their databases. Any reviewer with direct access should correct whatever they find.
2. **The CEFR describes performance levels, not a syllabus.** Any "grammar by level" list is an interpretation. This one included.
3. **The assignments marked `PD>B2` are the weakest.** They constitute the designer's judgement, not a source. They are flagged so the reviewer examines them first.

---

## 2. Content architecture

### 2.1 The five objects

| Object | What it is | How many |
|---|---|---|
| **Skill** | Grammatical, functional or lexical topic | 47 in English, 47 in German |
| **Topic** | Conversation subject | 14, shared across languages |
| **VocabPack** | Vocabulary for one Topic in one language and level | up to 140 |
| **Occurrence** | One concrete appearance of a Skill at a level, with its depth | variable |
| **Week** | Computed assignment of (Skill, Topic, Occurrence) to an ISO week | 52–53 per year |

Golden rule: **the first four are permanent, the fifth is computed.** The calendar is not written, it is derived. Hence there is no mandatory annual maintenance.

### 2.2 One bank per language, not one per level

Decision taken: there is a single Skill bank per language. Each Skill declares the levels at which it applies. The user's level filters which Skills they see and adjusts three things:

1. **The depth of the occurrence** (subtitle, focus).
2. **The vocabulary pack** of the associated Topic.
3. **The challenge type and the evidence** required.

What does *not* change is the Skill. Konjunktiv II is Konjunktiv II at B1 and at C1; what changes is what is done with it.

This avoids the error of maintaining five parallel banks that drift out of sync.

### 2.3 Temporal indexing

ISO 8601 week. Content indexed by `(isoYear, isoWeek)`. The ISO year may not match the calendar year at the December/January boundaries. This is resolved with the language's native function, never by hand.

### 2.4 "Magazine" mode

The device date governs. Week 14 is week 14 whether or not the user completed week 13. There is no progress pointer, no saved state, no recovery of missed weeks. It is the only variant compatible with "the only device data read is the date".

Accepted consequence: if the user is away for three weeks, those three weeks are lost. In exchange, the application does not chase or shame them, and stores nothing about them.

### 2.5 No per-week file structure

No project file is named "week 12". Content lives indexed by `(skill, level, order)`. The calendar only references IDs. Reordering the year means changing a function, not rewriting content.

---

## 3. Challenge types and evidence by level

This is the section that makes A2 work inside an open-practice application. Without it, A2 would be "the same but easier", which does not work: an A2 learner cannot write 150 free words using a structure, and has no way of knowing whether they got it right.

The solution is that **the challenge type changes with the level**, not merely its difficulty.

| Level | Challenge type | Logic | Evidence |
|---|---|---|---|
| **A2** | `chunk_deployment` | A closed repertoire of fixed phrases, deployed in a new context each time | 5–8 written sentences or 60–90 s of audio |
| **B1** | `guided_production` | Scaffolding: the frame is given, the user completes and extends | 100–150 words or 2 min of audio |
| **B2** | `constrained_production` | Free production with a declared structural constraint | 250 words or 4 min of audio |
| **C1** | `open_production` | Free production; the constraint is register and precision, not form | 400 words or 6 min of audio |
| **C2** | `adaptive_production` | The same idea reformulated for three different audiences or registers | 400–600 words or 8 min of audio |

### 3.1 Why A2 does fit

The argument against was that the application asks for free production and an A2 learner cannot produce freely. The argument in favour, which is the correct one: at A2 the learner acquires a limited repertoire of ready-made phrases that solve concrete situations, and the problem is not learning them but **daring to use them and sustaining them in shifting contexts**.

So at A2:

- The repertoire is **closed and visible**. The card supplies 10–12 chunks, not a rule.
- What rotates is the **context**, not the content. The same chunks for "asking for something" are used at the bakery, the pharmacy and the post office.
- The challenge is deployment, not creation.
- New vocabulary enters gradually and always attached to a chunk already mastered.

This inverts the hierarchy relative to the higher levels:

| | What leads | What supports |
|---|---|---|
| A2 – B1 | The **Topic** (the situation) | The Skill |
| B2 – C2 | The **Skill** (the structure) | The Topic |

This is a real design difference, not a nuance. An A2 card looks different from a C1 card.

### 3.2 Micro-tasks

Each occurrence carries **3 micro-tasks** (Monday / Wednesday / Friday), one line each, derived from the same Skill. They are not new content: they are three angles on the same challenge. They exist because a purely weekly application is opened on Monday and forgotten for the other six days.

### 3.3 Correction prompt

Each occurrence carries a pre-written prompt, copyable to the clipboard, to paste into any AI assistant. This is what closes the feedback loop without the application connecting to anything.

Prompt rules:
- **Exclusive** focus on the week's Skill. Not "correct everything": that returns a useless list of commas.
- Ask for correct uses to be marked, not only errors.
- Ask for a more natural alternative, not only the correction.
- At B2 and above, also ask for a register assessment.

### 3.4 Integration weeks

- **Skill Review Week** every 8 weeks (ISO weeks 8, 16, 24, 32, 40, 48). No new Skill: the last eight are recombined.
- **Survival Week** every ~16 weeks (weeks 12, 28, 44), offset from the above. No Skill: one long communicative task using whatever is available.
- Both languages align these weeks. The rest of the year runs on independent tracks.

---

## 4. Skill bank — English (47)

The levels shown are those at which the Skill is **actively practised** in the application, not those at which it first appears in a course. A Skill may keep appearing above its acquisition level: `Second Conditional` is acquired at B1 but is still worked on at B2, because fluency with it takes years.

### 4.1 Function and fluency (18)

| ID | Skill | Levels | Anchor | Note |
|---|---|---|---|---|
| EN-F01 | Giving and asking for opinions | A2 B1 B2 C1 C2 | CV Informal/Formal discussion | A2: closed repertoire (*I think…*). C1: qualified opinion under pressure |
| EN-F02 | Agreeing and disagreeing | A2 B1 B2 C1 C2 | CV Formal discussion (meetings) | A2: simple agreement. C1: diplomatic disagreement |
| EN-F03 | Describing people, places and routines | A2 B1 | CV Sustained monologue: describing experience | Descriptive base; drops out at B2 |
| EN-F04 | Telling a story / recounting experiences | A2 B1 B2 C1 C2 | CV Sustained monologue: describing experience | The axis shifts: A2 sequence, C1 narrative structure and tension |
| EN-F05 | Making and responding to suggestions | A2 B1 B2 | CV Goal-oriented co-operation | |
| EN-F06 | Survival: directions, help, emergencies | A2 | CV Information exchange | A2 only. Closed repertoire |
| EN-F07 | Survival: transactions (shops, tickets, appointments) | A2 | CV Goal-oriented co-operation | A2 only. Closed repertoire |
| EN-F08 | Comparing and contrasting | A2 B1 B2 C1 | CV Sustained monologue: giving information | A2: comparatives. C1: qualified and concessive contrast |
| EN-F09 | Giving advice | B1 B2 C1 | CV Goal-oriented co-operation | Overlaps with EN-G06 and EN-G20 |
| EN-F10 | Speculating and predicting | B1 B2 C1 C2 | CV Propositional precision | Grammatical base in EN-G10 |
| EN-F11 | Summarising complex information | B1 B2 C1 C2 | CV Relaying specific information / Processing text | Companion Volume mediation scale |
| EN-F12 | Persuading and negotiating | B2 C1 C2 | CV Sustained monologue: putting a case | |
| EN-F13 | Hedging and softening claims | B2 C1 C2 | CV Propositional precision | Pragmatic descriptor, not grammatical. Core of professional English |
| EN-F14 | Giving constructive feedback | B2 C1 C2 | CV Facilitating collaborative interaction | High workplace value, poorly covered by courses |
| EN-F15 | Managing a conversation: turn-taking, interrupting, repair | B1 B2 C1 C2 | CV Turntaking / Taking the floor | The most underrated skill for spoken production |
| EN-F16 | Complaining and resolving problems | B1 B2 C1 | CV Goal-oriented co-operation | |
| EN-F17 | Register shifting: formal ↔ informal | B2 C1 C2 | CV Sociolinguistic appropriateness | |
| EN-F18 | Irony, understatement and humour | C1 C2 | CV Sociolinguistic appropriateness | Markedly cultural. High risk of unvalidatable content |

### 4.2 Grammar (20)

| ID | Skill | Levels | Anchor | Note |
|---|---|---|---|---|
| EN-G01 | Present simple vs. present continuous | A2 | EGP A2 | |
| EN-G02 | Past simple and past continuous | A2 B1 | EGP A2 | |
| EN-G03 | Present perfect vs. past simple | A2 B1 B2 | EGP A2/B1 | Classic fossilised error for Spanish speakers |
| EN-G25 | Past perfect and narrative time reference | B1 B2 | EGP B1 + corpus | **Gap detected**: 37 EGP entries and total absence from A2 texts |
| EN-G04 | Future forms: will / going to / present continuous | A2 B1 | EGP A2/B1 | |
| EN-G05 | First conditional | A2 B1 | EGP A2 | |
| EN-G06 | Second conditional | B1 B2 | EGP B1 | |
| EN-G07 | Third conditional | B1 B2 | EGP B1 | |
| EN-G09 | Modals of obligation, permission and ability | A2 B1 | EGP A2 | |
| EN-G10 | Modals of deduction (*must have*, *might have*, *can't have*) | B1 B2 C1 | EGP B1/B2 | |
| EN-G11 | Passive voice | B1 B2 | EGP B1 | |
| EN-G12 | Impersonal and advanced passive (*it is said that…*) | C1 | EGP C1 | |
| EN-G13 | Reported speech | B1 B2 | EGP B1 | |
| EN-G14 | Relative clauses: defining and non-defining | A2 B1 B2 | EGP A2 | Non-defining with *who*/*which* is already A2 |
| EN-G15 | Reduced relative and participle clauses | B2 C1 C2 | EGP B1+ | Large jump in written density |
| EN-G16 | Inversion, cleft and emphatic structures | C1 C2 | EGP C1 | |
| EN-G17 | Gerunds and infinitives | B1 B2 C1 | EGP A1–C2 | |
| EN-G18 | Articles and quantifiers | A2 B1 | EGP A2 | Error persisting to C1 in Spanish speakers |
| EN-G19 | Word order and adverb placement | B1 B2 C1 | EGP A1–C2 | |
| EN-G20 | Unreal past: *I wish*, *if only*, *it's time* | B2 C2 | EGP B2/C2 | |

*`EN-G08 Mixed conditionals` was removed: zero matches across the 1,222 EGP structures. It is a course-book category, not a corpus-attested criterial feature. See `AUDITORIA-EGP.md`.*

### 4.3 Lexis (9)

| ID | Skill | Levels | Anchor | Note |
|---|---|---|---|---|
| EN-V01 | Fixed chunks for daily survival | A2 | CV Vocabulary range | Core of the A2 model. Closed repertoire per Topic |
| EN-V02 | Everyday phrasal verbs | A2 B1 | CV Vocabulary range | |
| EN-V03 | Advanced phrasal verbs | B2 C1 C2 | CV Vocabulary range | |
| EN-V04 | Everyday collocations | B1 B2 | CV Vocabulary control | |
| EN-V05 | Professional and academic collocations | C1 C2 | CV Vocabulary control | |
| EN-V06 | Idioms in context | B2 C1 C2 | CV Vocabulary control | Risk of dated register. Native review mandatory |
| EN-V07 | Discourse markers and linking | B1 B2 C1 C2 | CV Coherence and cohesion | |
| EN-V08 | Precision: avoiding overused words | B2 C1 C2 | CV Propositional precision | *very*, *thing*, *good*, *a lot* |
| EN-V09 | Connotation and nuance | C1 C2 | CV Vocabulary control | |

### 4.4 Resulting distribution

| Level | Skills available | Fluency | Grammar | Lexis |
|---|---|---|---|---|
| A2 | 18 | 8 | 8 | 2 |
| B1 | 27 | 10 | 12 | 5 |
| B2 | 30 | 12 | 12 | 6 |
| C1 | 29 | 13 | 8 | 8 |
| C2 | 17 | 9 | 2 | 6 |

Reading: the weight shifts from grammar to fluency and lexis as the level rises, which is exactly what should happen. At C2 only two grammatical items remain, because there is no new grammar left to learn — only grammar left to master.

---

## 5. Skill bank — German (47)

Reminder from section 1.2: **above B2 there is no new grammar.** Profile deutsch describes German grammar only up to B2 and assumes that at C1/C2 the work is active use, not structure acquisition. The bank reflects this: the C levels carry register, precision, stylistics and variation.

### 5.1 Function and fluency / Kommunikation (14)

| ID | Skill | Levels | Anchor | Note |
|---|---|---|---|---|
| DE-F01 | Meinung äußern und begründen | A2 B1 B2 C1 C2 | PD / CEFR | A2: closed *Ich finde…*. C1: qualified argumentation |
| DE-F02 | Zustimmen und widersprechen | A2 B1 B2 C1 C2 | PD | C1: *höflicher Widerspruch*, central in a Swiss workplace |
| DE-F03 | Alltag bewältigen: Einkauf, Termine, Behörden | A2 | Goethe A2 | A2 only. Closed repertoire |
| DE-F04 | Erfahrungen und Geschichten erzählen | A2 B1 B2 C1 | PD | |
| DE-F05 | Vorschläge machen und aushandeln | A2 B1 B2 C1 | PD | |
| DE-F06 | Ratschläge geben | B1 B2 C1 | PD | Overlaps with DE-G09 |
| DE-F07 | Vermutungen ausdrücken | B1 B2 C1 C2 | PD | Overlaps with DE-G05 (subjective modal use) |
| DE-F08 | Zusammenfassen | B1 B2 C1 C2 | CEFR | Mediation scale |
| DE-F09 | Argumentieren und überzeugen | B2 C1 C2 | PD B2 | |
| DE-F10 | Abschwächen und relativieren | B2 C1 C2 | PD>B2 | German equivalent of hedging. Leans heavily on Modalpartikeln |
| DE-F11 | Gespräch steuern: einwerfen, nachfragen, korrigieren | B1 B2 C1 C2 | CEFR | |
| DE-F12 | Sich beschweren und reklamieren | A2 B1 B2 C1 | PD | |
| DE-F13 | Register: du/Sie, formell/informell | B1 B2 C1 C2 | PD | |
| DE-F14 | Ironie, Untertreibung und Humor | C1 C2 | PD>B2 | High validation debt |

### 5.2 Grammar / Grammatik (24)

| ID | Skill | Levels | Anchor | Note |
|---|---|---|---|---|
| DE-G01 | Satzbau: Haupt- und Nebensatz, Verbstellung | A2 B1 B2 | PD A2 | The costliest structural error for Spanish speakers |
| DE-G02 | Konnektoren (*weil, obwohl, trotzdem, deshalb, während*) | A2 B1 B2 C1 | PD A2/B1 | C1: concessive and consecutive subtleties |
| DE-G03 | Präpositionen: Dativ, Akkusativ, Wechselpräpositionen | A2 B1 | PD A2 | |
| DE-G04 | Trennbare und untrennbare Verben | A2 B1 | PD A2 | |
| DE-G05 | Modalverben | A2 B1 B2 C1 | PD A2 / Aspekte C1 | Subjective use (*Er soll reich sein*) is **C1**, not B2 |
| DE-G06 | Perfekt vs. Präteritum im Gespräch | A2 B1 | PD A2/B1 | |
| DE-G07 | Adjektivdeklination | A2 B1 B2 | PD A2 | An error that survives to C1 |
| DE-G08 | Verben mit festen Präpositionen + Präpositionaladverbien | B1 B2 | PD B1 | *worauf, darauf, damit* |
| DE-G09 | Konjunktiv II | B1 B2 C1 | PD B1 | |
| DE-G10 | Passiv und Passiversatzformen | B1 B2 C1 | PD B1/B2 | C1: *lassen sich*, *-bar*, *ist zu + Inf.* |
| DE-G11 | Relativsätze | B1 B2 C1 | PD B1 | C1: genitive relatives, *wo-/was-* |
| DE-G12 | Nominalisierung und Nominalstil | B2 C1 | PD B2 | Gateway to technical and administrative German |
| DE-G13 | Infinitivsätze mit *zu* | B1 B2 C1 | PD B1 / Aspekte C1 | C1: present and past |
| DE-G14 | Erweiterte Partizipialkonstruktionen | B2 C1 | Aspekte B2/C1 | Begins at B2 (*Partizipien als Adjektive*) |
| DE-G15 | Konjunktiv I und indirekte Rede | B2 C1 | Aspekte B2 | Aspekte introduces it at B2, not C1 |
| DE-G16 | Genitiv und Genitivpräpositionen | B2 C1 | PD B2 | |
| DE-G17 | Negation und Fokuspartikeln | B2 | Aspekte B2 | Aspekte places it at B2, not B1+ |
| DE-G18 | Temporale Nebensätze (*als, wenn, während, nachdem, seitdem*) | A2 B1 B2 | PD A2/B1 | *als* vs *wenn*: classic error |
| DE-G19 | Vergleichssätze (*als, wie, je … desto*) | B1 B2 | Aspekte B2 | Gap detected: there was no comparison skill in German |
| DE-G20 | Das Wort *es* (positional and correlative) | B2 | Aspekte B2 | Frequent error, rarely taught |
| DE-G21 | Modalitätsverben (*scheinen, pflegen, drohen*) | C1 | Aspekte C1 | Genuinely C1 |
| DE-G22 | Reflexive Verben | B1 B2 | Aspekte B1+ | **Serious gap**: the German set of reflexive verbs does not match the Spanish one |
| DE-G23 | n-Deklination und Pluralbildung der Nomen | B1 | Aspekte B1+ | The n-declension has no Spanish equivalent |
| DE-G24 | Zukünftiges ausdrücken (Präsens vs. Futur I) | B1 | Aspekte B1+ | |

### 5.3 Lexis / Wortschatz (9)

| ID | Skill | Levels | Anchor | Note |
|---|---|---|---|---|
| DE-V01 | Alltags-Chunks | A2 | Goethe A2 | Core of the A2 model |
| DE-V02 | Redewendungen | B1 B2 C1 C2 | PD B1+ | |
| DE-V03 | Kollokationen | B1 B2 C1 | PD B1+ | |
| DE-V04 | Funktionsverbgefüge | B2 C1 | Aspekte B2 | Aspekte calls it *Nomen-Verb-Verbindungen*, at B2 |
| DE-V05 | Modalpartikeln (*doch, mal, ja, eben, halt, wohl*) | B1 B2 C1 C2 | PD B1 | **The single highest-impact skill for sounding natural.** Barely taught |
| DE-V06 | Wortbildung: Präfixe, Suffixe, Komposita | B1 B2 C1 | PD B1 | Multiplies vocabulary without memorising lists |
| DE-V07 | Umgangssprache vs. Standardsprache | B2 C1 C2 | PD>B2 | |
| DE-V08 | Helvetismen und Deutschschweizer Kontext | B1 B2 C1 C2 | — | Own addition, outside the CEFR. See note 5.5 |
| DE-V09 | Feine Bedeutungsunterschiede und Konnotation | C1 C2 | PD>B2 | |

### 5.4 Resulting distribution

| Level | Skills available | Fluency | Grammar | Lexis |
|---|---|---|---|---|
| A2 | 14 | 6 | 8 | 1 |
| B1 | 31 | 9 | 17 | 5 |
| B2 | 35 | 11 | 18 | 6 |
| C1 | 28 | 12 | 9 | 7 |
| C2 | 13 | 8 | 0 | 5 |

**German C2 has zero grammatical items.** This is not an oversight: it is the direct consequence of what Profile deutsch states. If a reviewer asks for grammar to be added there, they must be asked for the source.

### 5.5 Note on DE-V08 (Helvetismen)

This Skill has no anchor in any CEFR reference, because the CEFR describes standard German. It is included for a practical reason: the reference user of this edition lives and works in German-speaking Switzerland, where written Hochdeutsch coexists with spoken Swiss German and with helvetisms that are fully standard in formal register (*Velo*, *parkieren*, *Traktandum*, *Unterlagen einreichen*, absence of *ß*).

This is **regionally marked** content. In a public distribution this Skill must carry a variant label and be switchable off — the schema's `variante` field serves that purpose — or it will mislead a learner in another German-speaking area.

### 5.6 Overlap between banks

Twelve Skills have an almost exact equivalent in both languages (giving opinions, disagreeing, summarising, narrating, advising, speculating, negotiating, managing conversation, register, passive, relative clauses, conditional/Konjunktiv II). The overlap is desirable: it lets the user recognise the communicative function and concentrate effort on the form.

Nine are language-specific and must not be forced onto the other: Modalpartikeln, Funktionsverbgefüge, Nominalisierung, Adjektivdeklination and Helvetismen have no useful English equivalent; phrasal verbs, articles/quantifiers and unreal past have no useful German equivalent.

---

## 6. Topic bank (14) and depth ladder

The 14 Topics are shared across languages and levels. What changes with the level is **the angle from which the subject is approached** and the associated vocabulary, not the subject itself.

Design rule: the A2 angle is always **transactional** (solving a situation), B1 **descriptive-personal**, B2 **argumentative**, C1 **analytical-professional** and C2 **critical-abstract**.

### T01 — Work & Career / Arbeit und Beruf

| Level | Angle | Typical task |
|---|---|---|
| A2 | Job, hours, workplace | Describe a working day |
| B1 | Job changes, interviews, conditions | Recount a change of employment |
| B2 | Work-life balance, remote work, hierarchy | Defend a position on the four-day week |
| C1 | Organisational culture, conflict, leadership | Give feedback to a difficult colleague |
| C2 | The meaning of work, automation, class | Assess public discourse on productivity |

### T02 — Personal Finance & Banking / Finanzen und Bankwesen

| Level | Angle | Typical task |
|---|---|---|
| A2 | Paying, receiving, opening an account | Complete a transaction at the bank |
| B1 | Budget, saving, fixed costs | Explain how one's own spending is organised |
| B2 | Debt, insurance, retirement | Argue about renting vs. buying |
| C1 | Investment, risk, pension systems | Explain a pension system to a foreigner |
| C2 | Inequality, monetary policy, speculation | Criticise a public economic argument |

### T03 — Technology & AI / Technologie und KI

| Level | Angle | Typical task |
|---|---|---|
| A2 | Devices, apps, simple problems | Ask for help with a phone that won't work |
| B1 | Digital habits, social media, basic privacy | Recount how one's phone use changed |
| B2 | Automation, employment, regulation | Debate whether AI should replace repetitive tasks |
| C1 | Algorithmic bias, governance, dependency | Explain a technical risk to a non-technical audience |
| C2 | Technological determinism, digital sovereignty | Critically assess a technological promise |

### T04 — Health & Wellbeing / Gesundheit und Wohlbefinden

| Level | Angle | Typical task |
|---|---|---|
| A2 | Symptoms, doctor, pharmacy, appointment | Book an appointment and describe a complaint |
| B1 | Habits, sleep, exercise, diet | Describe a change of habit |
| B2 | Health systems, prevention, mental health | Argue about healthcare coverage |
| C1 | Medical ethics, ageing, evidence | Summarise a study for a lay reader |
| C2 | Medicalisation, autonomy, limits of science | Assess a bioethical debate |

### T05 — Travel & Culture Shock / Reisen und Kulturschock

| Level | Angle | Typical task |
|---|---|---|
| A2 | Tickets, hotel, directions, luggage | Solve a problem at a station |
| B1 | Trips taken, comparing places | Narrate a trip with something going wrong |
| B2 | Mass tourism, integration, prejudice | Defend a position on tourism |
| C1 | Identity, migration, culture shock | Explain a cultural misunderstanding of one's own |
| C2 | Exoticism, postcolonialism, belonging | Criticise a tourism narrative |

### T06 — Environment & Sustainability / Umwelt und Nachhaltigkeit

| Level | Angle | Typical task |
|---|---|---|
| A2 | Recycling, transport, daily weather | Explain how waste is separated |
| B1 | Sustainable habits, consumption | Recount a change of habit and its reason |
| B2 | Climate policy, costs, responsibility | Argue about carbon taxes |
| C1 | Energy transition, offsets, evidence | Explain a real technical trade-off |
| C2 | Degrowth, climate justice, green discourse | Assess the greenwashing in a campaign |

### T07 — Housing & City Life / Wohnen und Stadtleben

| Level | Angle | Typical task |
|---|---|---|
| A2 | Home, neighbourhood, rent, moving | Report a fault to the landlord |
| B1 | Flat hunting, cohabitation, transport | Compare two neighbourhoods of previous residence |
| B2 | Prices, speculation, urban planning | Argue about rent controls |
| C1 | Gentrification, planning, density | Present an urban project and its drawbacks |
| C2 | Right to the city, property, exclusion | Criticise an urban plan through its language |

### T08 — Relationships & Family / Beziehungen und Familie

| Level | Angle | Typical task |
|---|---|---|
| A2 | Family, friends, plans | Invite someone and arrange details |
| B1 | Friendship at a distance, minor conflicts | Recount how one met someone |
| B2 | Roles, expectations, parenting | Argue about the division of household tasks |
| C1 | Conflict, boundaries, care | Mediate in someone else's disagreement |
| C2 | Family models, intimacy, social change | Analyse a generational shift |

### T09 — Education & Lifelong Learning / Bildung und Weiterbildung

| Level | Angle | Typical task |
|---|---|---|
| A2 | Studies, timetable, teachers | Ask about a course and enrol |
| B1 | Learning strategies, difficulties | Describe one's own study method |
| B2 | Education systems, exams, access | Argue about free education |
| C1 | Continuing education, retraining, merit | Design and defend a training plan |
| C2 | Credentialism, inequality, autonomy | Criticise the discourse of talent |

### T10 — Food & Cooking / Essen und Kochen

| Level | Angle | Typical task |
|---|---|---|
| A2 | Ordering, shopping, simple cooking | Order in a restaurant with a restriction |
| B1 | Recipes, tastes, cuisine of origin | Explain a typical dish step by step |
| B2 | Diet, industry, labelling | Argue about meat and sustainability |
| C1 | Supply chains, culinary culture | Explain a food controversy |
| C2 | Authenticity, appropriation, food and class | Assess the discourse of "authentic" food |

### T11 — Current Events & News / Aktuelles und Nachrichten

| Level | Angle | Typical task |
|---|---|---|
| A2 | Simple headlines, weather, incidents | Recount something that happened this week |
| B1 | Summarising a news item, reacting | Summarise a news item in five sentences |
| B2 | Contrasting coverage, opining | Compare two versions of the same event |
| C1 | Framing, sources, bias | Analyse how an outlet constructs a narrative |
| C2 | Disinformation, agenda, political rhetoric | Dismantle a public argument |

**Design warning:** T11 is the only Topic that ages. Tasks must be phrased generically ("a news item from this week"), never with concrete events, or the 2026 edition becomes unusable in 2028.

### T12 — Hobbies & Free Time / Hobbys und Freizeit

| Level | Angle | Typical task |
|---|---|---|
| A2 | Activities, frequency, company | Propose a plan and agree a time |
| B1 | Getting into the hobby, motives | Recount how one came to an interest |
| B2 | Free time, productivity, digital leisure | Argue about "useful" leisure |
| C1 | Community, identity, amateurism | Explain a subculture from the inside |
| C2 | Leisure and class, commodification of free time | Analyse the professionalisation of a hobby |

### T13 — Services & Bureaucracy / Dienstleistungen und Behörden

| Level | Angle | Typical task |
|---|---|---|
| A2 | Post office, bank, hairdresser, appointment | Complete an in-person formality |
| B1 | Contracts, complaints, customer service | Relate a formality that went wrong |
| B2 | Bureaucracy, digitalisation, access | Argue about public vs. private services |
| C1 | Administration, rights, mediation | Explain a procedure to a newcomer |
| C2 | State, citizenship, institutional opacity | Criticise the design of an administrative system |

### T14 — Consumption & Commerce / Konsum und Handel

| Level | Angle | Typical task |
|---|---|---|
| A2 | Buying, returning, comparing prices | Return a faulty item |
| B1 | Online shopping, warranties, advertising | Describe a purchase one regrets |
| B2 | Responsible consumption, obsolescence | Argue about repairing vs. replacing |
| C1 | Supply chains, market power | Explain why something costs what it costs |
| C2 | Consumerism, manufactured desire, value | Critically analyse an advertising campaign |

### 6.1 Origin of T13 and T14

Both were added when the bank was cross-checked against the thematic catalogue the Goethe-Institut publishes in its B2 handbook, taken from the CEFR: 14 areas, among them *Dienstleistungen* and *Konsum und Handel*, which were missing. **T03 (Technology & AI) and T11 (Current Events) are not in that catalogue**: they are own additions, justified because the CEFR catalogue dates from 2001 and did not anticipate AI or present-day news consumption. This is declared so that a reviewer knows which Topics have an anchor and which do not.

### 6.2 Note on universality for expansion to ES/FR/IT/PT

Twelve of the fourteen Topics are reasonably universal. Three are not, and need watching when translating:

- **T02 (Finance):** pension, insurance and banking systems differ so much between countries that B2+ vocabulary is nearly untranslatable without local variants. The Swiss pillar does not exist in Argentina.
- **T07 (Housing):** rent, deposits, ownership and regulation are radically different by country.
- **T13 (Services and bureaucracy):** the most country-dependent of all. Swiss formalities resemble neither Argentine nor German ones.

Recommendation: when a language is added, these three Topics need their own vocabulary pack, not a translated one.

---

## 7. Vocabulary packs

### 7.0 Selection criterion

The packs do **not** attempt to cover a topic's semantic field. A dictionary does that better and for free. Items are selected if they meet at least one of these criteria:

1. **Not inferable** from Spanish (collocations, verbs with a fixed preposition).
2. **False friends** or register traps.
3. **High real frequency** in conversation on that topic, low presence in textbooks.
4. **Chunks** ready to use, not isolated words.

Target size: **14 items per pack**, of which four are marked ★ as the core and the remaining ten are extension. A larger pack does not get used, it gets skimmed.

### 7.1 Coverage status

| | A2 | B1 | B2 | C1 | C2 |
|---|---|---|---|---|---|
| English | specified | specified | specified | **complete (14/14)** | specified |
| German | specified | specified | **complete (14/14)** | specified | specified |

The 28 complete packs correspond to the levels of the reference user of this edition (English C1, German B2), the only ones validatable in own use and the only ones needed for the 2026 pilot. The remaining levels have their angle and criterion specified (section 6); their packs will be written when a reviewer able to validate them is available. Writing 112 unvalidatable packs now would be exactly the error this project decided not to make.

### 7.2 Note on the language of the packs

**The pack tables are carried over unchanged from the Spanish edition, and this is deliberate.** Two reasons:

1. The `ES` column is a **base-language gloss**, a property of the *edition* (English C1 and German B2 for a Spanish-speaking user), not of the language this document is written in. Replacing it with English glosses would not produce a translation of this document: it would produce a different edition, for a different user.
2. The usage notes annotate contrasts with Spanish (*false friend*, *not inferable from Spanish*) which only make sense against that base language.

The schema keeps `traducciones` as a map of five languages, deliberately empty for those not yet written. When an English-base edition exists, it fills the `en` key and these tables regenerate automatically. See sections 2.1 and 8.

For the full pack tables, see sections 7.2 and 7.3 of `syllabus-app-idiomas.md` (Spanish edition), which are the current authoritative data.

---

## 8. Licences

### 8.1 Preliminary clarification: a licence certifies nothing

The requirement was "the licence that prevents anyone copying it and selling it with ads, and that certifies there is no tracking or in-app purchasing". Two things must be separated, because they are not the same:

| What is wanted | What achieves it |
|---|---|
| That nobody makes a closed version with ads | **The licence** (copyleft) |
| That the absence of trackers can be demonstrated | **Distribution and audit**, not the licence |

No free-software licence forbids charging for software. What strong copyleft does is oblige any distributed derivative to publish its source under the same licence. That does not prevent selling, but it **destroys the business model of an ad-supported fork**: whoever attempts it must publish the code, and anyone can recompile it without the ads in five minutes.

### 8.2 Application code: **GPL-3.0-only**

Reasons:

- Strong copyleft: any distributed derivative must publish its source under GPL-3.0.
- It includes anti-tivoisation and patent clauses that AGPL shares but MIT does not.
- It is the dominant licence in the free Android app ecosystem, which makes component reuse easier.

AGPL is ruled out: its added value is covering network-service use, and this application has no network by design. It would add friction without benefit.

MIT is ruled out: it would permit exactly the closed ad-supported fork the project wants to prevent.

### 8.3 Pedagogical content: **CC BY-SA 4.0**

Content is not code and the GPL fits it badly. CC BY-SA 4.0:

- Requires attribution and that derivative works keep the same licence.
- It is the standard licence for open educational content, which matters once translations into French or Italian arrive: it obliges them to return to the commons.
- In practice it is compatible with distributing the content inside a GPL application.

**Consequence to accept:** vocabulary and descriptions must be original. Goethe-Institut lists and English Vocabulary Profile entries cannot be copied — they are protected. They may be used as a methodological reference and cited, which is what this document does.

### 8.4 How the absence of trackers is demonstrated

This is not done by the licence but by publication on **F-Droid**, which maintains a public system of *Anti-Features*: labels visible on each app's page indicating whether it contains advertising, tracks user activity, depends on non-free network services, promotes proprietary add-ons, and several more.

The *Tracking* label applies to apps that record or report user activity without permission or by default, including cases as mild as sending crash reports or checking for updates without notice. F-Droid also builds from source, so the label does not depend on the author's word.

The project's stated goal is therefore concrete and verifiable: **zero Anti-Features on F-Droid**. This is achieved as follows:

- No `INTERNET` permission in the manifest. This is the strongest available proof: if the app cannot open a socket, it cannot track anything. It is worth more than any privacy policy.
- No Google Play Services, no Firebase, no analytics or crash-reporting library.
- Reproducible builds, so the published APK can be verified against the source.
- Additionally, an **Exodus Privacy** report showing zero trackers.

### 8.5 What must be written before the first public commit

| File | Content |
|---|---|
| `LICENSE` | Full GPL-3.0 text |
| `LICENSE-CONTENT` | Full CC BY-SA 4.0 text, with explicit scope (everything under `/content`) |
| `CONTRIBUTING.md` | That contributors accept CC BY-SA 4.0 for content and GPL-3.0 for code |
| `PRIVACY.md` | One sentence: the app has no network permission and collects no data |

The point about `CONTRIBUTING.md` is the most urgent: if the first French translation is accepted without a clear licence, permission must afterwards be sought individually from every contributor for any change.

---

## 9. Known debts

This document declares its own weaknesses, ordered by severity.

| # | Debt | Risk | When to resolve |
|---|---|---|---|
| 0 | German audit | **CLOSED for grammar.** Cross-checked against Aspekte neu B1+/B2/C1 and Netzwerk neu A2/B1: 6 corrections, 6 new skills, no errors at A2. The 23 fluency and lexis skills remain uncovered by any index | Non-blocking |
| 1 | No native speaker has reviewed the English bank | Dated or unnatural register, especially in EN-V06 (idioms) and EN-F18 (irony) | Before publication |
| 2 | Review by a native speaker or teacher of German | Desirable quality improvement | **Indefinite. Does not block the application** |
| 3 | Vocabulary packs: 28 of 140 written | Levels A2, B1, English B2 and German C1 are not yet usable | When a reviewer per level is available |
| 4 | DE-V08 (Helvetismen) has no CEFR anchor | Confuses users outside Switzerland | Resolved in the schema via the `variante` field |
| 5 | ~~Verification against the EGP~~ | **CLOSED.** Full audit against the 1,222 EGP structures; 4 corrections applied and 1 skill removed | Done |
| 6 | T11 (Current Events) ages | A given year's edition becomes obsolete | Rule already fixed: never concrete events in tasks |
| 7 | German B2+ vocabulary | No official list exists. Extracting from graded texts works as a method, but the available corpus is regional-tourism material and yields only place names | Open |

### 9.1 What this document deliberately does NOT do

- It does not assign concrete weeks. That is computed by the calendar generator.
- It does not write cards. That is the next step.
- It does not define UI, stack or widget.
- It does not include content for levels that cannot be validated by the user or by an available reviewer.

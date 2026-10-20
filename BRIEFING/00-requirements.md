# Spinellis Group Project — Requirements (captured verbatim)

> Theme date: 13-1-2027. Captured from the assignment brief. This is the source
> of truth; we work the briefing from this.

## Θέμα • Έξυπνοι ανελκυστήρες
Σύστημα προεπιλογής προορισμού — **destination dispatch** — για ανελκυστήρες.

Αποτελείται από:
- **Κεντρικό σύστημα ελέγχου** (central control system)
- **Διεπαφή χρήστη** (user interface)
- **Σύστημα ελέγχου κάθε ανελκυστήρα** (per-elevator control system)
- **Σύστημα δοκιμών και αξιολόγησης** (testing & evaluation system)

Cross-cutting:
- **Τυποποίηση διεπαφών και δοκιμών (ομαδικά)** — standardization of interfaces
  and tests, done as a GROUP.
- **Ρεαλιστική δοκιμή** — με πραγματικούς χρήστες, διαλειτουργικότητας
  (realistic test: with real users, interoperability).
- **Τεχνική αναφορά** (technical report).
- **Προωθητικό βίντεο** (promotional video).

## Ελάχιστες απαιτήσεις — Κεντρικό σύστημα ελέγχου (central control)
- Εκτελείται από τη γραμμή εντολών (runs from the command line).
- Παρέχει τυποποιημένη διεπαφή REST:
    - Από χρήστες (from users)
    - Προς ανελκυστήρες, οθόνες (to elevators, displays)
- Υποστηρίζει **2–20 ανελκυστήρες, 3–200 ορόφους**.
- Διαμόρφωση παραμέτρων λειτουργίας (configurable operating parameters).
- Αποστολή εντολών σε ανελκυστήρες (sends commands to elevators).

## Ελάχιστες απαιτήσεις — Διεπαφή χρήστη (user interface)
- Εκτελείται από τη γραμμή εντολών.
- Δέχεται ως παραμέτρους τον **όροφο εγκατάστασης** (install floor as argument).
- Παρέχει τυποποιημένη διεπαφή REST από και προς το κεντρικό σύστημα ελέγχου.
- Επιτρέπει την εισαγωγή του **ορόφου προορισμού** (enter destination floor).
- Εμφανίζει το αντίστοιχο **αναγνωριστικό γράμμα του ανελκυστήρα**
  (shows the assigned elevator's identifier letter).

## Ελάχιστες απαιτήσεις — Σύστημα ελέγχου ανελκυστήρα (per-elevator control)
- Εκτελείται από τη γραμμή εντολών.
- Δέχεται ως παραμέτρους το **αναγνωριστικό γράμμα του ανελκυστήρα**
  (elevator identifier letter as argument).
- Παρέχει τυποποιημένη διεπαφή REST:
    - Από κεντρικό σύστημα ελέγχου (from central control)
    - Προς κινητήρα, θύρες, οθόνες, σύστημα αξιολόγησης
      (to motor, doors, displays, evaluation system)
- Διαμόρφωση παραμέτρων λειτουργίας.
- Εμφάνιση κατάστασης, του ορόφου που βρίσκεται και των επόμενων στάσεων
  (shows status, current floor, next stops).

## Ελάχιστες απαιτήσεις — Σύστημα αξιολόγησης (evaluation system)
- Εκτελείται από τη γραμμή εντολών.
- Δέχεται ως παραμέτρους τα στοιχεία λειτουργίας και αξιολόγησης
  (operating & evaluation data as arguments).
- Παρέχει τυποποιημένη διεπαφή REST:
    - Προς κεντρικό σύστημα ελέγχου (to central control)
    - Από ανελκυστήρες (from elevators)
- Εκτελεί σενάρια λειτουργίας (runs operating scenarios).
- Εμφανίζει αποτελέσματα της αξιολόγησης (shows evaluation results).

## Τυποποίηση (standardization)
- Συνεργασία όλων των ομάδων (all teams collaborate).
- Προδιαγραφές διεπαφών REST (REST interface specs).
- Καθορισμός μεθόδων αξιολόγησης απόδοσης (define performance evaluation methods).
- Εκτέλεση της αξιολόγησης (run the evaluation).

## Προαιρετικές πρόσθετες λειτουργίες (optional extras)
- Γραφική διεπαφή (π.χ. ανά υποσύστημα) — GUI, e.g. per subsystem.
- Κεντρικός πίνακας ελέγχου — central dashboard.
- Εφαρμογή κινητού τηλεφώνου — mobile app.
- Διεπαφή με έξυπνη κάρτα — smart card interface.
- Διεπαφή με υλικό — hardware interface.
- …

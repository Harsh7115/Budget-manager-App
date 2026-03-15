/**
 * ExpenseManager manages the expense list, provides filtering, searching,
 * and categorization, and integrates with BudgetManager for budget tracking.
 */
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExpenseManager {

    private final List<Expense> expenses;
    private final BudgetManager budgetManager;
    private Runnable guiUpdateCallback;

    public static final List<String> predefinedCategories = List.of(
        "Food", "Transportation", "Entertainment", "Utilities", "Miscellaneous"
    );

    /** Constructs an ExpenseManager with an empty expense list. */
    public ExpenseManager() {
        this.expenses      = new ArrayList<>();
        this.budgetManager = new BudgetManager(this);
    }

    // -----------------------------------------------------------------------
    // Callback plumbing
    // -----------------------------------------------------------------------

    public void setGuiUpdateCallback(Runnable callback) { this.guiUpdateCallback = callback; }
    public Runnable getGuiUpdateCallback()              { return guiUpdateCallback; }

    public void triggerUpdate() {
        if (guiUpdateCallback != null) guiUpdateCallback.run();
    }

    // -----------------------------------------------------------------------
    // CRUD operations
    // -----------------------------------------------------------------------

    /**
     * Adds a validated expense to the list.
     *
     * @param expense the expense to add; must be non-null with amount &gt; 0
     * @throws IllegalArgumentException if the expense or its amount is invalid
     */
    public void addExpense(Expense expense) {
        validateExpense(expense);
        expenses.add(expense);
        updateBudgets();
        triggerUpdate();
    }

    /**
     * Replaces the expense at {@code index} with {@code newExpense}.
     *
     * @throws IndexOutOfBoundsException if the index is out of range
     * @throws IllegalArgumentException  if newExpense is invalid
     */
    public void editExpense(int index, Expense newExpense) {
        checkIndex(index);
        validateExpense(newExpense);
        expenses.set(index, newExpense);
        updateBudgets();
        triggerUpdate();
    }

    /**
     * Removes the expense at {@code index}.
     *
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public void deleteExpense(int index) {
        checkIndex(index);
        expenses.remove(index);
        updateBudgets();
        triggerUpdate();
    }

    // -----------------------------------------------------------------------
    // Queries
    // -----------------------------------------------------------------------

    /** Returns an unmodifiable view of all expenses. */
    public List<Expense> getAllExpenses() { return expenses; }

    /** Returns the total number of recorded expenses. */
    public int getExpenseCount() { return expenses.size(); }

    /**
     * Returns the sum of all expense amounts.
     *
     * @return total expenses, or 0.0 if the list is empty
     */
    public double getTotalExpenses() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }

    /** Filters expenses by category (case-insensitive). */
    public List<Expense> filterByCategory(String category) {
        if (category == null || category.isBlank()) return new ArrayList<>(expenses);
        return expenses.stream()
            .filter(e -> e.getCategory().equalsIgnoreCase(category))
            .collect(Collectors.toList());
    }

    /** Filters expenses whose date falls within [start, end] inclusive. */
    public List<Expense> filterByDateRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) throw new IllegalArgumentException("Date range bounds must not be null");
        return expenses.stream()
            .filter(e -> !e.getDate().isBefore(start) && !e.getDate().isAfter(end))
            .collect(Collectors.toList());
    }

    /**
     * Searches expenses whose description contains {@code keyword} (case-insensitive).
     *
     * @param keyword the substring to search for
     * @return matching expenses, or all expenses if keyword is blank
     */
    public List<Expense> searchByDescription(String keyword) {
        if (keyword == null || keyword.isBlank()) return new ArrayList<>(expenses);
        String lower = keyword.toLowerCase();
        return expenses.stream()
            .filter(e -> e.getDescription() != null && e.getDescription().toLowerCase().contains(lower))
            .collect(Collectors.toList());
    }

    /** Returns expenses for the given calendar month. */
    public List<Expense> getExpensesForMonth(YearMonth yearMonth) {
        return expenses.stream()
            .filter(e -> YearMonth.from(e.getDate()).equals(yearMonth))
            .collect(Collectors.toList());
    }

    /** Calculates total spending in {@code category} for {@code yearMonth}. */
    public double calculateMonthlyExpensesByCategory(String category, YearMonth yearMonth) {
        return getExpensesForMonth(yearMonth).stream()
            .filter(e -> e.getCategory().equals(category))
            .mapToDouble(Expense::getAmount)
            .sum();
    }

    /** Returns a map from each predefined category to its monthly total. */
    public Map<String, Double> getMonthlyTotalsByCategory(YearMonth yearMonth) {
        Map<String, Double> totals = new HashMap<>();
        for (String cat : predefinedCategories) {
            totals.put(cat, calculateMonthlyExpensesByCategory(cat, yearMonth));
        }
        return totals;
    }

    public static List<String> getPredefinedCategories() { return predefinedCategories; }

    /** Returns the BudgetManager, wiring the update callback. */
    public BudgetManager getBudgetManager() {
        budgetManager.setUpdateCallback(this::triggerUpdate);
        return budgetManager;
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    private void validateExpense(Expense expense) {
        if (expense == null) throw new IllegalArgumentException("Expense must not be null");
        if (expense.getAmount() <= 0) throw new IllegalArgumentException("Expense amount must be positive");
        if (expense.getCategory() == null || expense.getCategory().isBlank())
            throw new IllegalArgumentException("Expense category must not be blank");
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= expenses.size())
            throw new IndexOutOfBoundsException("Invalid expense index: " + index);
    }

    private void updateBudgets() {
        budgetManager.getAllBudgets(YearMonth.now());
    }
}

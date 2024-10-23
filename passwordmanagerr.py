import tkinter as tk
from tkinter import ttk, messagebox
from cryptography.fernet import Fernet
import os


# Generate and/or load encryption key
def load_key():
    """Load or generate a key for encrypting passwords"""
    if not os.path.exists('key.key'):
        key = Fernet.generate_key()
        with open('key.key', 'wb') as key_file:
            key_file.write(key)
    else:
        with open('key.key', 'rb') as key_file:
            key = key_file.read()
    return key

key = load_key()
cipher = Fernet(key)

# Password Manager Functions
def add_password():
    """Add a new password entry"""
    website = website_entry.get()
    username = username_entry.get()
    password = password_entry.get()

    if not website or not username or not password:
        messagebox.showerror("Error", "All fields are required!")
        return

    encrypted_password = cipher.encrypt(password.encode()).decode()

    with open('passwords.txt', 'a') as f:
        f.write(f"{website} | {username} | {encrypted_password}\n")

    clear_entries()
    update_password_list()
    messagebox.showinfo("Success", "Password saved!")


def view_passwords():
    """View saved passwords"""
    update_password_list()

def search_password():
    """Search for a password based on website"""
    search_query = website_entry.get()
    
    if not search_query:
        messagebox.showerror("Error", "Enter a website to search!")
        return
    
    found = False
    password_list.delete(1.0, tk.END)

    if not os.path.exists('passwords.txt'):
        messagebox.showerror("Error", "No passwords found!")
        return

    with open('passwords.txt', 'r') as f:
        passwords = f.readlines()
        
        for entry in passwords:
            website, username, encrypted_password = entry.strip().split(' | ')
            if website.lower() == search_query.lower():
                decrypted_password = cipher.decrypt(encrypted_password.encode()).decode()
                password_list.insert(tk.END, f"Website: {website}\nUsername: {username}\nPassword: {decrypted_password}\n")
                found = True
                break

    if not found:
        messagebox.showinfo("No Result", f"No password found for {search_query}")

def delete_password():
    """Delete a password entry"""
    delete_query = website_entry.get()
    
    if not delete_query:
        messagebox.showerror("Error", "Enter a website to delete!")
        return
    
    if messagebox.askyesno("Confirm Deletion", f"Are you sure you want to delete the password for {delete_query}?"):
        if not os.path.exists('passwords.txt'):
            messagebox.showerror("Error", "No passwords found!")
            return

        with open('passwords.txt', 'r') as f:
            passwords = f.readlines()

        with open('passwords.txt', 'w') as f:
            found = False
            for entry in passwords:
                website, username, encrypted_password = entry.strip().split(' | ')
                if website.lower() != delete_query.lower():
                    f.write(entry)
                else:
                    found = True

        if found:
            messagebox.showinfo("Success", f"Password for {delete_query} deleted!")
            clear_entries()
            update_password_list()
        else:
            messagebox.showinfo("No Result", f"No password found for {delete_query}")

def update_password_list():
    """Update the password list in the text box with better formatting"""
    password_list.delete(1.0, tk.END)

    if not os.path.exists('passwords.txt'):
        return

    with open('passwords.txt', 'r') as f:
        passwords = f.readlines()

    for entry in passwords:
        website, username, encrypted_password = entry.strip().split(' | ')
        decrypted_password = cipher.decrypt(encrypted_password.encode()).decode()
        
        # Formatted output for better readability
        formatted_entry = (
            f"----------------------------------------\n"
            f"Website: {website}\n"
            f"Username: {username}\n"
            f"Password: {decrypted_password}\n"
            f"----------------------------------------\n"
        )
        password_list.insert(tk.END, formatted_entry)

def clear_entries():
    """Clear input fields"""
    website_entry.delete(0, tk.END)
    username_entry.delete(0, tk.END)
    password_entry.delete(0, tk.END)

# Function to toggle password visibility
def toggle_password():
    """Toggle the visibility of the password"""
    if show_password_var.get():
        password_entry.config(show="")
    else:
        password_entry.config(show="*")

# GUI Setup
root = tk.Tk()
root.title("Password Manager")
root.geometry("500x620")
root.configure(bg="#333333")  # Dark gray background

# Custom Fonts
header_font = ("Helvetica", 16, "bold")
label_font = ("Helvetica", 11)
entry_font = ("Helvetica", 10)

# Header
header = tk.Label(root, text="Password Manager", bg="#333333", fg="#FFFFFF", font=header_font, padx=10, pady=10)
header.place(x=160, y=20)

# Labels and Entries (with "card-like" design)
frame = tk.Frame(root, bg="#444444", relief=tk.RAISED, bd=3)
frame.place(x=50, y=80, width=400, height=170)

tk.Label(frame, text="Website:", bg="#444444", font=label_font, fg="#FFFFFF").place(x=20, y=20)
website_entry = tk.Entry(frame, font=entry_font, width=30, bd=2, relief=tk.GROOVE)
website_entry.place(x=120, y=20)

tk.Label(frame, text="Username:", bg="#444444", font=label_font, fg="#FFFFFF").place(x=20, y=60)
username_entry = tk.Entry(frame, font=entry_font, width=30, bd=2, relief=tk.GROOVE)
username_entry.place(x=120, y=60)

tk.Label(frame, text="Password:", bg="#444444", font=label_font, fg="#FFFFFF").place(x=20, y=100)
password_entry = tk.Entry(frame, font=entry_font, width=30, bd=2, relief=tk.GROOVE, show="*")
password_entry.place(x=120, y=100)

# Show Password Checkbox
show_password_var = tk.BooleanVar()
show_password_checkbox = tk.Checkbutton(frame, text="Show Password", variable=show_password_var, bg="#444444", fg="#FFFFFF", command=toggle_password)
show_password_checkbox.place(x=120, y=130)

# Button styling function with hover effect
def on_hover(event, btn, original_color, hover_color):
    btn['bg'] = hover_color

def on_leave(event, btn, original_color, hover_color):
    btn['bg'] = original_color

def create_rounded_button(text, command, x, y, original_color, hover_color):
    button = tk.Button(root, text=text, command=command, font=("Helvetica", 10, "bold"), bg=original_color, fg="white", bd=0, relief=tk.RAISED, width=18, height=2)
    button.place(x=x, y=y)
    button.bind("<Enter>", lambda e: on_hover(e, button, original_color, hover_color))
    button.bind("<Leave>", lambda e: on_leave(e, button, original_color, hover_color))
    return button

create_rounded_button("Add Password", add_password, 80, 270, "#4CAF50", "#45A049")  # Green button
create_rounded_button("View Passwords", view_passwords, 260, 270, "#4CAF50", "#45A049")
create_rounded_button("Search Password", search_password, 80, 330, "#2196F3", "#1E88E5")  # Blue button
create_rounded_button("Delete Password", delete_password, 260, 330, "#F44336", "#E53935")  # Red button

# Textbox for showing passwords
password_list = tk.Text(root, height=10, width=52, font=('Arial', 10), bd=2, relief=tk.GROOVE, padx=10, pady=10)
password_list.place(x=50, y=400)
password_list.config(bg="#555555", fg="#FFFFFF")  # Darker background for better contrast

# Scrollbar for text widget
scrollbar = ttk.Scrollbar(root, command=password_list.yview)
password_list.config(yscrollcommand=scrollbar.set)
scrollbar.place(x=450, y=400, height=165)

# Adding a header label for the password display area
password_header = tk.Label(root, bg="#333333", fg="#FFFFFF", font=("Helvetica", 12, "bold"))
password_header.place(x=50, y=370)

# Start the GUI event loop
root.mainloop()
// // Add this at the VERY top of admin.js
// (function checkAuth() {
//     const currentPage = window.location.pathname.split("/").pop();
//
//     // Only redirect if we ARE NOT on the login page
//     if (currentPage !== "signIn.html" && currentPage !== "") {
//         const isAdmin = localStorage.getItem('isAdminLoggedIn');
//         if (isAdmin !== 'true') {
//             window.location.href = "signIn.html";
//         }
//     }
// })();
//
// // Logout function for your Sidebar "Logout" button
// function logout() {
//     localStorage.removeItem('isAdminLoggedIn');
//     window.location.href = "signIn.html";
// }
//
// const API_BASE_URL1 = 'http://localhost:8080/AndroidProjectAPI/api/user';
//
//
// document.getElementById('signInForm').addEventListener('submit', async (e) => {
//     e.preventDefault();
//
//     const btnText = document.getElementById('btnText');
//     const btnLoading = document.getElementById('btnLoading');
//     const errorMsg = document.getElementById('signInError');
//
//     // Show Loading
//     btnText.classList.add('d-none');
//     btnLoading.classList.remove('d-none');
//     errorMsg.classList.add('d-none');
//
//     const email = document.getElementById('adminEmail').value;
//     const password = document.getElementById('adminPassword').value;
//
//     try {
//         const response = await fetch(`${API_BASE_URL1}/check-login`, {
//             method: 'POST',
//             headers: { 'Content-Type': 'application/json' },
//             body: JSON.stringify({ email, password })
//         });
//
//         if (response.ok) {
//             const user = await response.json();
//
//             // 1. Check if the user is actually an ADMIN
//             if (user.userRole && user.userRole.role === "ADMIN") {
//                 localStorage.setItem('isAdminLoggedIn', 'true');
//                 localStorage.setItem('adminUser', JSON.stringify(user));
//                 window.location.href = "index.html";
//             } else {
//                 throw new Error("Access Denied: You are not an Admin.");
//             }
//         } else {
//             throw new Error("Invalid email or password.");
//         }
//     } catch (err) {
//         // Show Error
//         btnText.classList.remove('d-none');
//         btnLoading.classList.add('d-none');
//         errorMsg.innerText = err.message;
//         errorMsg.classList.remove('d-none');
//     }
// });
//
// // Inside your login fetch success:
// if (data.userRole.role === "ADMIN") {
//     localStorage.setItem('isAdminLoggedIn', 'true');
//     localStorage.setItem('userRole', 'ADMIN'); // Save the role
//     window.location.href = "admin.html";
// }
// /**
//  * Global Configuration
//  */
// const API_BASE_URL = 'http://localhost:8080/AndroidProjectAPI/api/admin';
//
//
// /**
//  * Main Entry Point - Consolidates all page logic
//  */
// document.addEventListener('DOMContentLoaded', () => {
//     // 1. Always load the sidebar on every admin page
//     loadSidebar();
//
//     // 2. Dashboard Logic: Only runs if the chart canvas exists
//     if (document.getElementById('orderChart')) {
//         fetchDashboardData();
//     }
//
//     // 3. Vendor Management Logic: Only runs if the vendor table exists
//     if (document.getElementById('vendorTableBody')) {
//         loadVendorTable();
//         loadUserDropdown();
//
//         // Attach form listener for adding new vendors
//         const vendorForm = document.getElementById('addVendorForm');
//         if (vendorForm) {
//             setupVendorFormListener(vendorForm);
//         }
//     }
//
//     if (document.getElementById('userTableBody')) {
//         loadUserTable();
//     }
//
//     if (document.getElementById('productTableBody')) {
//         loadProductTable();
//     }
//
//     if (document.getElementById('orderTableBody')) {
//         loadOrderTable();
//     }
// });
//
// /**
//  * Sidebar Logic
//  */
// function loadSidebar() {
//     const sidebarContainer = document.getElementById('sidebar-container');
//     if (sidebarContainer) {
//         fetch('sidebar.html')
//             .then(response => {
//                 if (!response.ok) throw new Error("Sidebar file not found");
//                 return response.text();
//             })
//             .then(data => {
//                 sidebarContainer.innerHTML = data;
//                 if (window.lucide) lucide.createIcons();
//                 highlightActiveLink();
//             })
//             .catch(err => console.error("Error loading sidebar:", err));
//     }
// }
//
// function highlightActiveLink() {
//     // 1. Get the current page filename
//     let currentPage = window.location.pathname.split("/").pop();
//
//     // Handle the case where the URL ends in "/" (usually the dashboard)
//     if (currentPage === "" || currentPage === "admin") {
//         currentPage = "index.html";
//     }
//
//     const navLinks = document.querySelectorAll('.nav-link');
//
//     navLinks.forEach(link => {
//         // 2. CRITICAL: Remove 'active' from ALL links first
//         link.classList.remove('active');
//
//         // 3. Add 'active' ONLY to the matching link
//         if (link.getAttribute('href') === currentPage) {
//             link.classList.add('active');
//         }
//     });
// }
//
// /**
//  * Dashboard / Statistics Logic
//  */
// async function fetchDashboardData() {
//     try {
//         const response = await fetch(`${API_BASE_URL}/stats`);
//         if (!response.ok) throw new Error("Failed to fetch stats");
//
//         const data = await response.json();
//
//         // Update UI counters
//         if (document.getElementById('statVendors')) document.getElementById('statVendors').innerText = data.totalVendors;
//         if (document.getElementById('statOrders')) document.getElementById('statOrders').innerText = data.activeOrders;
//         if (document.getElementById('statUsers')) document.getElementById('statUsers').innerText = data.totalUsers;
//         if (document.getElementById('statRevenue')) document.getElementById('statRevenue').innerText = data.revenue;
//
//         initDashboardChart(data.weeklyOrders);
//     } catch (error) {
//         console.error("Dashboard Load Error:", error);
//         initDashboardChart([0, 0, 0, 0, 0, 0, 0]);
//     }
// }
//
// function initDashboardChart(chartData) {
//     const chartCanvas = document.getElementById('orderChart');
//     if (!chartCanvas) return;
//
//     const ctx = chartCanvas.getContext('2d');
//     if (window.myDashboardChart) window.myDashboardChart.destroy();
//
//     window.myDashboardChart = new Chart(ctx, {
//         type: 'line',
//         data: {
//             labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
//             datasets: [{
//                 label: 'Orders',
//                 data: chartData,
//                 borderColor: '#0d6efd',
//                 backgroundColor: 'rgba(13, 110, 253, 0.05)',
//                 fill: true,
//                 tension: 0.4
//             }]
//         },
//         options: {
//             responsive: true,
//             maintainAspectRatio: false,
//             plugins: {legend: {display: false}},
//             scales: {
//                 y: {beginAtZero: true},
//                 x: {grid: {display: false}}
//             }
//         }
//     });
// }
//
// /**
//  * Vendor Management Logic
//  */
// async function loadVendorTable() {
//     try {
//         const response = await fetch(`${API_BASE_URL}/vendors`);
//         const vendors = await response.json();
//         renderVendorTable(vendors);
//     } catch (error) {
//         console.error("Failed to load vendors:", error);
//     }
// }
//
// function renderVendorTable(vendors) {
//     const tableBody = document.getElementById('vendorTableBody');
//     if (!tableBody) return;
//
//     tableBody.innerHTML = '';
//
//     vendors.forEach(vendor => {
//         const vId = vendor.id || 'N/A';
//         const vName = vendor.name || 'Unnamed Business';
//         const vEmail = vendor.email || 'No Email';
//         const vPhone = vendor.phone || 'No Phone';
//         const isVerified = (vendor.is_verified == 1); // Check for numeric 1 or boolean true
//
//         let statusBadge = isVerified
//             ? `<span class="badge bg-success-subtle text-success px-3 py-2 rounded-pill"><i data-lucide="check-circle" style="width:12px"></i> Verified</span>`
//             : `<span class="badge bg-warning-subtle text-warning px-3 py-2 rounded-pill"><i data-lucide="clock" style="width:12px"></i> Pending</span>`;
//
//         let actionButton = isVerified
//             ? `<button class="btn btn-outline-primary btn-sm px-3">View Profile</button>`
//             : `<button class="btn btn-success btn-sm px-3 shadow-sm" onclick="verifyVendor(${vId})">Verify Now</button>`;
//
//         tableBody.innerHTML += `
//             <tr>
//                 <td><div class="fw-bold text-dark">${vName}</div><small class="text-muted">ID: #${vId}</small></td>
//                 <td>${vEmail}</td>
//                 <td>${vPhone}</td>
//                 <td>${statusBadge}</td>
//                 <td class="text-end">${actionButton}</td>
//             </tr>`;
//     });
//
//     if (window.lucide) lucide.createIcons();
// }
//
// /**
//  * Verification Action
//  */
// async function verifyVendor(vendorId) {
//     if (!confirm("Are you sure you want to verify this business?")) return;
//
//     try {
//         const response = await fetch(`${API_BASE_URL}/vendor-verify/${vendorId}`, {
//             method: 'POST'
//         });
//
//         if (response.ok) {
//             alert("Business Verified!");
//             loadVendorTable(); // Refresh only the table
//         } else {
//             alert("Verification failed on server.");
//         }
//     } catch (error) {
//         alert("Network error during verification.");
//     }
// }
//
// /**
//  * Add Vendor Form Logic
//  */
// async function loadUserDropdown() {
//     const select = document.getElementById('vUserId');
//     if (!select) return;
//
//     try {
//         const response = await fetch(`${API_BASE_URL}/users`);
//         const users = await response.json();
//
//         select.innerHTML = '<option value="">Select User...</option>';
//         users.forEach(user => {
//             select.innerHTML += `<option value="${user.id}">${user.id} - ${user.firstName} ${user.lastName}</option>`;
//         });
//     } catch (error) {
//         console.error("Error loading users:", error);
//     }
// }
//
// function setupVendorFormListener(form) {
//     form.addEventListener('submit', async (e) => {
//         e.preventDefault();
//
//         // 1. Data Collection
//         const userId = document.getElementById('vUserId').value;
//         const lat = document.getElementById('vLatitude').value;
//         const lng = document.getElementById('vLongitude').value;
//
//         // 2. Simple Validation
//         if (!userId) return alert("Please select a User ID.");
//         if (lat < -90 || lat > 90 || lng < -180 || lng > 180) return alert("Invalid GPS coordinates.");
//
//         const vendorData = {
//             userId: parseInt(userId),
//             name: document.getElementById('vName').value.trim(),
//             description: document.getElementById('vDescription').value.trim(),
//             phone: document.getElementById('vPhone').value.trim(),
//             latitude: parseFloat(lat),
//             longitude: parseFloat(lng)
//         };
//
//         try {
//             const response = await fetch(`${API_BASE_URL}/vendor-add`, {
//                 method: 'POST',
//                 headers: {'Content-Type': 'application/json'},
//                 body: JSON.stringify(vendorData)
//             });
//
//             if (response.ok) {
//                 alert("Vendor Added Successfully!");
//                 const modalEl = document.getElementById('addVendorModal');
//                 const modal = bootstrap.Modal.getInstance(modalEl);
//                 if (modal) modal.hide();
//
//                 loadVendorTable();
//                 e.target.reset();
//             } else {
//                 const result = await response.json();
//                 alert("Error: " + (result.error || "Failed to add vendor"));
//             }
//         } catch (error) {
//             console.error("Critical Error:", error);
//         }
//     });
// }
//
// /**
//  * User Management Section
//  */
//
// // 1. Load Users from API
// async function loadUserTable() {
//     const tableBody = document.getElementById('userTableBody');
//     if (!tableBody) return;
//
//     try {
//         const response = await fetch(`${API_BASE_URL}/users`);
//         const users = await response.json();
//
//         // Store users globally for the search function to use
//         window.allUsers = users;
//         renderUserTable(users);
//     } catch (error) {
//         console.error("Failed to load users:", error);
//         tableBody.innerHTML = '<tr><td colspan="4" class="text-center text-danger">Failed to load user data.</td></tr>';
//     }
// }
//
// // 2. Render the Table (Includes Block/Unblock Logic)
// function renderUserTable(users) {
//     const tableBody = document.getElementById('userTableBody');
//     tableBody.innerHTML = '';
//
//     if (users.length === 0) {
//         tableBody.innerHTML = '<tr><td colspan="4" class="text-center py-4">No users found.</td></tr>';
//         return;
//     }
//
//     users.forEach(user => {
//         const isActive = (user.userStatus.id == 2);
//
//         // Safely handle names to prevent "undefined undefined"
//         const fullName = (user.firstName || user.lastName)
//             ? `${user.firstName || ''} ${user.lastName || ''}`.trim()
//             : "N/A";
//
//         const statusBadge = isActive
//             ? `<span class="badge bg-success-subtle text-success px-3 py-2 rounded-pill">Active</span>`
//             : `<span class="badge bg-danger-subtle text-danger px-3 py-2 rounded-pill">Blocked</span>`;
//
//         const actionBtn = isActive
//             ? `<button class="btn btn-outline-danger btn-sm px-3" onclick="toggleUserStatus(${user.id}, 3)">Block</button>`
//             : `<button class="btn btn-success btn-sm px-3 shadow-sm" onclick="toggleUserStatus(${user.id}, 2)">Unblock</button>`;
//
//         tableBody.innerHTML += `
//         <tr class="animate-in">
//             <td class="px-4">
//                 <div class="fw-bold text-dark">${fullName}</div>
//                 <div class="small text-muted">${user.email}</div>
//                 <small class="text-muted" style="font-size: 10px;">ID: #${user.id}</small>
//             </td>
//             <td class="px-4 text-secondary">
//                 ${user.userRole ? user.userRole.role : 'User'}
//             </td>
//             <td class="px-4">${statusBadge}</td>
//             <td class="px-4 text-end">
//                 <div class="d-flex justify-content-end gap-2">
//                     ${actionBtn}
//                 </div>
//             </td>
//         </tr>`;
//     });
//
//     if (window.lucide) lucide.createIcons();
// }
//
// // 3. Real-time Search Logic
// // 3. Real-time Search Logic
// const searchInput = document.getElementById('userSearch');
// if (searchInput) {
//     searchInput.addEventListener('input', (e) => {
//         const searchTerm = e.target.value.toLowerCase();
//
//         // Check if data exists before filtering
//         if (!window.allUsers) return;
//
//         const filteredUsers = window.allUsers.filter(user => {
//             // Create a temporary full name string for searching
//             const firstName = (user.firstName || "").toLowerCase();
//             const lastName = (user.lastName || "").toLowerCase();
//             const email = (user.email || "").toLowerCase();
//             const id = user.id ? user.id.toString() : "";
//
//             // Check if the search term matches name, email, or ID
//             return firstName.includes(searchTerm) ||
//                 lastName.includes(searchTerm) ||
//                 email.includes(searchTerm) ||
//                 id.includes(searchTerm);
//         });
//
//         renderUserTable(filteredUsers);
//     });
// }
//
// // 4. Toggle Status Function (API Call)
// async function toggleUserStatus(userId, status) {
//     const action = status === 2 ? "Unblock" : "Block";
//     if (!confirm(`Are you sure you want to ${action} this user?`)) return;
//
//     try {
//         const response = await fetch(`${API_BASE_URL}/users/status/${userId}?active=${status}`, {
//             method: 'POST'
//         });
//
//         if (response.ok) {
//             alert(`User ${action}ed successfully!`);
//             loadUserTable(); // Refresh the list
//         } else {
//             alert("Error updating user status.");
//         }
//     } catch (error) {
//         console.error("Status Update Error:", error);
//     }
// }
//
// /**
//  * ==========================================
//  * PRODUCT MANAGEMENT SECTION
//  * ==========================================
//  */
//
// // 1. Load Products and Update Dashboard Stats
// async function loadProductTable() {
//     const tableBody = document.getElementById('productTableBody');
//     if (!tableBody) return;
//
//     try {
//         const response = await fetch(`${API_BASE_URL}/products`);
//         const products = await response.json();
//
//         // Store globally for the search bar
//         window.allProducts = products;
//
//         // Render the table and update the top counters
//         renderProductTable(products);
//         updateProductStats(products);
//
//     } catch (error) {
//         console.error("Failed to load products:", error);
//         tableBody.innerHTML = '<tr><td colspan="5" class="text-center text-danger py-4">Failed to load inventory data.</td></tr>';
//     }
// }
//
// // 2. Render the Table Rows
// function renderProductTable(products) {
//     const tableBody = document.getElementById('productTableBody');
//     tableBody.innerHTML = '';
//
//     if (!products || products.length === 0) {
//         tableBody.innerHTML = '<tr><td colspan="5" class="text-center text-muted py-4">No products found in inventory.</td></tr>';
//         return;
//     }
//
//     products.forEach(product => {
//         // Format price to LKR
//         const priceFormatted = parseFloat(product.price).toLocaleString('en-LK', {style: 'currency', currency: 'LKR'});
//
//         // Handle Category Name safely
//         const categoryName = product.category ? product.category.name : 'Uncategorized';
//
//         // Visual warning if stock is low (e.g., less than 10)
//         const stockDisplay = product.stockQuantity < 10
//             ? `<span class="text-danger fw-bold"><i data-lucide="alert-circle" style="width: 14px; margin-top:-2px;"></i> ${product.stockQuantity}</span>`
//             : `<span class="text-success fw-medium">${product.stockQuantity}</span>`;
//
//         tableBody.innerHTML += `
//             <tr class="animate-in">
//                 <td class="px-4 fw-bold text-dark">${product.name}</td>
//                 <td class="px-4"><span class="badge bg-light text-dark border">${categoryName}</span></td>
//                 <td class="px-4 text-primary fw-medium">${priceFormatted}</td>
//                 <td class="px-4">${stockDisplay}</td>
//             </tr>`;
//     });
//
//     if (window.lucide) lucide.createIcons();
// }
//
// // 3. Calculate Dashboard Stats dynamically
// function updateProductStats(products) {
//     const totalCountEl = document.getElementById('totalProductsCount');
//     const lowStockCountEl = document.getElementById('lowStockCount');
//
//     if (totalCountEl && lowStockCountEl) {
//         totalCountEl.innerText = products.length;
//
//         // Filter out products that have less than 10 in stock
//         const lowStockItems = products.filter(p => p.stockQuantity < 10).length;
//         lowStockCountEl.innerText = lowStockItems;
//     }
// }
//
// // 4. Real-time Search Logic
// const productSearchInput = document.getElementById('productSearch');
// if (productSearchInput) {
//     productSearchInput.addEventListener('input', (e) => {
//         const searchTerm = e.target.value.toLowerCase();
//
//         if (!window.allProducts) return;
//
//         const filteredProducts = window.allProducts.filter(product => {
//             const name = (product.name || "").toLowerCase();
//             const cat = (product.category && product.category.name ? product.category.name : "").toLowerCase();
//
//             return name.includes(searchTerm) || cat.includes(searchTerm);
//         });
//
//         renderProductTable(filteredProducts);
//     });
// }
//
// /**
//  * ==========================================
//  * CATEGORY MANAGEMENT SECTION
//  * ==========================================
//  */
//
// // 5. Load Categories into the Modal list
// async function loadCategories() {
//     const listEl = document.getElementById('categoryList');
//     listEl.innerHTML = '<li class="list-group-item text-center text-muted">Loading...</li>';
//
//     try {
//         const response = await fetch(`${API_BASE_URL}/categories`);
//         const categories = await response.json();
//
//         listEl.innerHTML = '';
//         if (categories.length === 0) {
//             listEl.innerHTML = '<li class="list-group-item text-muted">No categories found.</li>';
//             return;
//         }
//
//         categories.forEach(cat => {
//             listEl.innerHTML += `
//                 <li class="list-group-item d-flex justify-content-between align-items-center">
//                     ${cat.name}
//                     <span class="badge bg-secondary rounded-pill">ID: ${cat.id}</span>
//                 </li>`;
//         });
//     } catch (error) {
//         listEl.innerHTML = '<li class="list-group-item text-danger">Error loading categories.</li>';
//     }
// }
//
// // 6. Save a New Category
// async function saveNewCategory() {
//     const nameInput = document.getElementById('newCatName');
//     const name = nameInput.value.trim();
//
//     if (!name) {
//         alert("Please enter a category name.");
//         return;
//     }
//
//     try {
//         const response = await fetch(`${API_BASE_URL}/save-categories`, {
//             method: 'POST',
//             headers: {'Content-Type': 'application/json'},
//             body: JSON.stringify({name: name})
//         });
//
//         if (response.ok) {
//             alert("Category Added Successfully!");
//             nameInput.value = ''; // Clear the input
//             loadCategories(); // Instantly refresh the list inside the modal!
//         } else {
//             alert("Failed to save category.");
//         }
//     } catch (error) {
//         console.error("Error saving category:", error);
//     }
// }
//
//
// /**
//  * ==========================================
//  * ORDER MANAGEMENT SECTION
//  * ==========================================
//  */
//
// // 1. Load Order Items and Group them by Order ID
// async function loadOrderTable() {
//     const tableBody = document.getElementById('orderTableBody');
//     if (!tableBody) return;
//
//     try {
//         const response = await fetch(`${API_BASE_URL}/orders`);
//         const orderItems = await response.json();
//
//         // Grouping logic: Create an object where keys are Order IDs
//         const groupedOrders = orderItems.reduce((acc, item) => {
//             const orderId = item.order.id;
//             if (!acc[orderId]) {
//                 acc[orderId] = {
//                     ...item.order,
//                     products: [] // Array to hold all products for this order
//                 };
//             }
//             acc[orderId].products.push({
//                 name: item.product.name,
//                 price: item.priceAtPurchase,
//                 qty: item.quantity,
//                 image: item.product.imageUrl
//             });
//             return acc;
//         }, {});
//
//         // Store globally for the modal
//         window.allOrders = Object.values(groupedOrders);
//         renderOrderTable(window.allOrders);
//
//         // Update stats
//         document.getElementById('totalProductsCount').innerText = window.allOrders.length;
//
//     } catch (error) {
//         console.error("Order Load Error:", error);
//         tableBody.innerHTML = '<tr><td colspan="5" class="text-center text-danger">Failed to load orders.</td></tr>';
//     }
// }
//
// // 2. Render the Table (One row per Order ID)
// function renderOrderTable(orders) {
//     const tableBody = document.getElementById('orderTableBody');
//     tableBody.innerHTML = '';
//
//     orders.forEach(order => {
//         const date = new Date(order.createdAt).toLocaleDateString();
//
//         tableBody.innerHTML += `
//             <tr>
//                 <td class="px-4 fw-bold text-primary">#ORD-${order.id}</td>
//                 <td class="px-4">
//                     <div class="fw-bold text-dark">${order.user.firstName} ${order.user.lastName}</div>
//                     <div class="small text-muted">${order.user.email}</div>
//                 </td>
//                 <td class="px-4 fw-bold text-dark">LKR ${order.totalAmount.toLocaleString()}</td>
//                 <td class="px-4">
//                     <span class="badge bg-info-subtle text-info px-3 py-2 rounded-pill">${order.orderStatus.status}</span>
//                 </td>
//                 <td class="px-4 text-end">
//                     <button class="btn btn-sm btn-outline-primary rounded-pill px-3" onclick="viewOrderDetails(${order.id})">
//                         View Details
//                     </button>
//                 </td>
//             </tr>`;
//     });
// }
//
// // 3. Populate Modal with ALL products in that order
// function viewOrderDetails(orderId) {
//     const order = window.allOrders.find(o => o.id === orderId);
//     if (!order) return;
//
//     // Shipping & Header
//     document.getElementById('detailOrderId').innerText = order.id;
//     document.getElementById('shipName').innerText = order.orderShipping.addressName;
//     document.getElementById('shipAddress').innerText = order.orderShipping.formattedAddress;
//     document.getElementById('detailTotal').innerText = `LKR ${order.totalAmount.toLocaleString()}`;
//
//     // List all products in this order
//     const itemsList = document.getElementById('orderItemsList');
//     itemsList.innerHTML = '';
//     const URL = "http://localhost:8080/AndroidProjectAPI/uploads/products/";
//     order.products.forEach(item => {
//         itemsList.innerHTML += `
//             <li class="list-group-item d-flex justify-content-between align-items-center py-3">
//                 <div class="d-flex align-items-center">
//                     <img src="${URL + item.image}" class="rounded me-3" style="width: 40px; height: 40px; object-fit: cover;">
//                     <div>
//                         <div class="fw-bold">${item.name}</div>
//                         <small class="text-muted">LKR ${item.price.toLocaleString()} x ${item.qty}</small>
//                     </div>
//                 </div>
//                 <div class="fw-bold text-dark">LKR ${(item.price * item.qty).toLocaleString()}</div>
//             </li>`;
//     });
//
//     new bootstrap.Modal(document.getElementById('orderDetailModal')).show();
// }
//
// document.addEventListener('DOMContentLoaded', () => {
//     // Initialize Icons
//     if (window.lucide) lucide.createIcons();
//
//     const signInForm = document.getElementById('signInForm');
//     const btnText = document.getElementById('btnText');
//     const btnLoading = document.getElementById('btnLoading');
//     const errorMsg = document.getElementById('signInError');
//
//     signInForm.addEventListener('submit', async (e) => {
//         e.preventDefault();
//
//         // UI Reset
//         btnText.classList.add('d-none');
//         btnLoading.classList.remove('d-none');
//         errorMsg.classList.add('d-none');
//
//         const email = document.getElementById('adminEmail').value;
//         const password = document.getElementById('adminPassword').value;
//
//         // Simulate API call delay
//         setTimeout(() => {
//             // For Demo/Submission:
//             if (email === "admin@test.com" && password === "1234") {
//                 localStorage.setItem('isAdminLoggedIn', 'true');
//                 window.location.href = "dashboard.html"; // Change this to your main admin file
//             } else {
//                 // Show Error
//                 btnText.classList.remove('d-none');
//                 btnLoading.classList.add('d-none');
//                 errorMsg.classList.remove('d-none');
//             }
//         }, 1200);
//     });
// });
// 1. Authentication Check (Runs immediately)
// 1. Authentication Check (Runs immediately)
(function checkAuth() {
    const currentPage = window.location.pathname.split("/").pop();

    // Only redirect if we ARE NOT on the login page
    if (currentPage !== "signIn.html" && currentPage !== "") {
        const isAdmin = localStorage.getItem('isAdminLoggedIn');
        if (isAdmin !== 'true') {
            window.location.href = "signIn.html";
        }
    }
})();

// Logout function for your Sidebar "Logout" button
function logout() {
    localStorage.removeItem('isAdminLoggedIn');
    localStorage.removeItem('adminUser');
    localStorage.removeItem('userRole');
    window.location.href = "signIn.html";
}

/**
 * Global Configuration
 */
const API_BASE_URL_USER = 'http://localhost:8080/AndroidProjectAPI/api/user';
const API_BASE_URL_ADMIN = 'http://localhost:8080/AndroidProjectAPI/api/admin';

/**
 * Helper to get auth headers
 */
function getAdminHeaders() {
    return {
        'User-Role': localStorage.getItem('userRole') || ''
    };
}

/**
 * Main Entry Point - Consolidates all page logic
 */
document.addEventListener('DOMContentLoaded', () => {
    // Initialize Icons globally if the library is loaded
    if (window.lucide) lucide.createIcons();

    // 1. Always load the sidebar on every admin page (except signIn)
    const sidebarContainer = document.getElementById('sidebar-container');
    if (sidebarContainer) {
        loadSidebar();
    }

    // 2. Sign In Logic: Only runs if the signIn form exists on the current page
    const signInForm = document.getElementById('signInForm');
    if (signInForm) {
        setupLogin(signInForm);
    }

    // 3. Dashboard Logic: Only runs if the chart canvas exists
    if (document.getElementById('orderChart')) {
        fetchDashboardData();
    }

    // 4. Vendor Management Logic
    if (document.getElementById('vendorTableBody')) {
        loadVendorTable();
        loadUserDropdown();

        const vendorForm = document.getElementById('addVendorForm');
        if (vendorForm) {
            setupVendorFormListener(vendorForm);
        }
    }

    // 5. User Management Logic
    if (document.getElementById('userTableBody')) {
        loadUserTable();
    }

    // 6. Product Management Logic
    if (document.getElementById('productTableBody')) {
        loadProductTable();
    }

    // 7. Order Management Logic
    if (document.getElementById('orderTableBody')) {
        loadOrderTable();
    }
});

/**
 * ==========================================
 * AUTHENTICATION / LOGIN SECTION
 * ==========================================
 */
function setupLogin(signInForm) {
    signInForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const btnText = document.getElementById('btnText');
        const btnLoading = document.getElementById('btnLoading');
        const errorMsg = document.getElementById('signInError');

        // Show Loading
        btnText.classList.add('d-none');
        btnLoading.classList.remove('d-none');
        errorMsg.classList.add('d-none');

        const email = document.getElementById('adminEmail').value;
        const password = document.getElementById('adminPassword').value;

        try {
            // Login does NOT need the User-Role header yet
            const response = await fetch(`${API_BASE_URL_USER}/check-login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password })
            });

            if (response.ok) {
                const user = await response.json();

                // Check if the user is actually an ADMIN
                if (user.userRole && user.userRole.role === "ADMIN") {
                    localStorage.setItem('isAdminLoggedIn', 'true');
                    localStorage.setItem('userRole', 'ADMIN');
                    localStorage.setItem('adminUser', JSON.stringify(user));
                    window.location.href = "index.html"; // Redirect to dashboard
                } else {
                    throw new Error("Access Denied: You are not an Admin.");
                }
            } else {
                throw new Error("Invalid email or password.");
            }
        } catch (err) {
            // Show Error
            btnText.classList.remove('d-none');
            btnLoading.classList.add('d-none');
            errorMsg.innerText = err.message;
            errorMsg.classList.remove('d-none');
        }
    });
}

/**
 * ==========================================
 * SIDEBAR LOGIC
 * ==========================================
 */
function loadSidebar() {
    const sidebarContainer = document.getElementById('sidebar-container');
    if (!sidebarContainer) return;

    fetch('sidebar.html')
        .then(response => {
            if (!response.ok) throw new Error("Sidebar file not found");
            return response.text();
        })
        .then(data => {
            sidebarContainer.innerHTML = data;
            if (window.lucide) lucide.createIcons();
            highlightActiveLink();
        })
        .catch(err => console.error("Error loading sidebar:", err));
}

function highlightActiveLink() {
    let currentPage = window.location.pathname.split("/").pop();
    if (currentPage === "" || currentPage === "admin") {
        currentPage = "index.html";
    }

    const navLinks = document.querySelectorAll('.nav-link');
    navLinks.forEach(link => {
        link.classList.remove('active');
        if (link.getAttribute('href') === currentPage) {
            link.classList.add('active');
        }
    });
}

/**
 * ==========================================
 * DASHBOARD LOGIC
 * ==========================================
 */
async function fetchDashboardData() {
    try {
        const response = await fetch(`${API_BASE_URL_ADMIN}/stats`, {
            headers: getAdminHeaders() // <-- ADDED HEADER
        });
        if (!response.ok) throw new Error("Failed to fetch stats");

        const data = await response.json();

        // Update UI counters safely
        if (document.getElementById('statVendors')) document.getElementById('statVendors').innerText = data.totalVendors;
        if (document.getElementById('statOrders')) document.getElementById('statOrders').innerText = data.activeOrders;
        if (document.getElementById('statUsers')) document.getElementById('statUsers').innerText = data.totalUsers;
        if (document.getElementById('statRevenue')) document.getElementById('statRevenue').innerText = data.revenue;

        initDashboardChart(data.weeklyOrders);
    } catch (error) {
        console.error("Dashboard Load Error:", error);
        initDashboardChart([0, 0, 0, 0, 0, 0, 0]);
    }
}

function initDashboardChart(chartData) {
    const chartCanvas = document.getElementById('orderChart');
    if (!chartCanvas) return;

    const ctx = chartCanvas.getContext('2d');
    if (window.myDashboardChart) window.myDashboardChart.destroy();

    // Ensure Chart.js is loaded in your HTML for this to work
    if (typeof Chart !== 'undefined') {
        window.myDashboardChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
                datasets: [{
                    label: 'Orders',
                    data: chartData,
                    borderColor: '#0d6efd',
                    backgroundColor: 'rgba(13, 110, 253, 0.05)',
                    fill: true,
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {legend: {display: false}},
                scales: {
                    y: {beginAtZero: true},
                    x: {grid: {display: false}}
                }
            }
        });
    }
}

/**
 * ==========================================
 * VENDOR MANAGEMENT SECTION
 * ==========================================
 */
async function loadVendorTable() {
    try {
        const response = await fetch(`${API_BASE_URL_ADMIN}/vendors`, {
            headers: getAdminHeaders() // <-- ADDED HEADER
        });
        const vendors = await response.json();
        renderVendorTable(vendors);
    } catch (error) {
        console.error("Failed to load vendors:", error);
    }
}

function renderVendorTable(vendors) {
    const tableBody = document.getElementById('vendorTableBody');
    if (!tableBody) return;

    tableBody.innerHTML = '';

    vendors.forEach(vendor => {
        const vId = vendor.id || 'N/A';
        const vName = vendor.name || 'Unnamed Business';
        const vEmail = vendor.email || 'No Email';
        const vPhone = vendor.phone || 'No Phone';
        const isVerified = (vendor.is_verified == 1);

        let statusBadge = isVerified
            ? `<span class="badge bg-success-subtle text-success px-3 py-2 rounded-pill"><i data-lucide="check-circle" style="width:12px"></i> Verified</span>`
            : `<span class="badge bg-warning-subtle text-warning px-3 py-2 rounded-pill"><i data-lucide="clock" style="width:12px"></i> Pending</span>`;

        let actionButton = isVerified
            ? `<button class="btn btn-outline-primary btn-sm px-3">View Profile</button>`
            : `<button class="btn btn-success btn-sm px-3 shadow-sm" onclick="verifyVendor(${vId})">Verify Now</button>`;

        tableBody.innerHTML += `
            <tr>
                <td><div class="fw-bold text-dark">${vName}</div><small class="text-muted">ID: #${vId}</small></td>
                <td>${vEmail}</td>
                <td>${vPhone}</td>
                <td>${statusBadge}</td>
                <td class="text-end">${actionButton}</td>
            </tr>`;
    });

    if (window.lucide) lucide.createIcons();
}

async function verifyVendor(vendorId) {
    if (!confirm("Are you sure you want to verify this business?")) return;

    try {
        const response = await fetch(`${API_BASE_URL_ADMIN}/vendor-verify/${vendorId}`, {
            method: 'POST',
            headers: getAdminHeaders() // <-- ADDED HEADER
        });

        if (response.ok) {
            alert("Business Verified!");
            loadVendorTable();
        } else {
            alert("Verification failed on server.");
        }
    } catch (error) {
        alert("Network error during verification.");
    }
}

async function loadUserDropdown() {
    const select = document.getElementById('vUserId');
    if (!select) return;

    try {
        const response = await fetch(`${API_BASE_URL_ADMIN}/users`, {
            headers: getAdminHeaders() // <-- ADDED HEADER
        });
        const users = await response.json();

        select.innerHTML = '<option value="">Select User...</option>';
        users.forEach(user => {
            select.innerHTML += `<option value="${user.id}">${user.id} - ${user.firstName} ${user.lastName}</option>`;
        });
    } catch (error) {
        console.error("Error loading users:", error);
    }
}

function setupVendorFormListener(form) {
    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const userId = document.getElementById('vUserId').value;
        const lat = document.getElementById('vLatitude').value;
        const lng = document.getElementById('vLongitude').value;

        if (!userId) return alert("Please select a User ID.");
        if (lat < -90 || lat > 90 || lng < -180 || lng > 180) return alert("Invalid GPS coordinates.");

        const vendorData = {
            userId: parseInt(userId),
            name: document.getElementById('vName').value.trim(),
            description: document.getElementById('vDescription').value.trim(),
            phone: document.getElementById('vPhone').value.trim(),
            latitude: parseFloat(lat),
            longitude: parseFloat(lng)
        };

        try {
            const response = await fetch(`${API_BASE_URL_ADMIN}/vendor-add`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'User-Role': localStorage.getItem('userRole') // <-- ADDED HEADER
                },
                body: JSON.stringify(vendorData)
            });

            if (response.ok) {
                alert("Vendor Added Successfully!");
                const modalEl = document.getElementById('addVendorModal');
                if (modalEl && typeof bootstrap !== 'undefined') {
                    const modal = bootstrap.Modal.getInstance(modalEl);
                    if (modal) modal.hide();
                }

                loadVendorTable();
                e.target.reset();
            } else {
                const result = await response.json();
                alert("Error: " + (result.error || "Failed to add vendor"));
            }
        } catch (error) {
            console.error("Critical Error:", error);
        }
    });
}

/**
 * ==========================================
 * USER MANAGEMENT SECTION
 * ==========================================
 */
async function loadUserTable() {
    const tableBody = document.getElementById('userTableBody');
    if (!tableBody) return;

    try {
        const response = await fetch(`${API_BASE_URL_ADMIN}/users`, {
            headers: getAdminHeaders() // <-- ADDED HEADER
        });
        const users = await response.json();

        window.allUsers = users;
        renderUserTable(users);
    } catch (error) {
        console.error("Failed to load users:", error);
        tableBody.innerHTML = '<tr><td colspan="4" class="text-center text-danger">Failed to load user data.</td></tr>';
    }
}

function renderUserTable(users) {
    const tableBody = document.getElementById('userTableBody');
    if (!tableBody) return;

    tableBody.innerHTML = '';

    if (users.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="4" class="text-center py-4">No users found.</td></tr>';
        return;
    }

    users.forEach(user => {
        // Safe check for userStatus object
        const isActive = user.userStatus && user.userStatus.id == 2;

        const fullName = (user.firstName || user.lastName)
            ? `${user.firstName || ''} ${user.lastName || ''}`.trim()
            : "N/A";

        const statusBadge = isActive
            ? `<span class="badge bg-success-subtle text-success px-3 py-2 rounded-pill">Active</span>`
            : `<span class="badge bg-danger-subtle text-danger px-3 py-2 rounded-pill">Blocked</span>`;

        const actionBtn = isActive
            ? `<button class="btn btn-outline-danger btn-sm px-3" onclick="toggleUserStatus(${user.id}, 3)">Block</button>`
            : `<button class="btn btn-success btn-sm px-3 shadow-sm" onclick="toggleUserStatus(${user.id}, 2)">Unblock</button>`;

        tableBody.innerHTML += `
        <tr class="animate-in">
            <td class="px-4">
                <div class="fw-bold text-dark">${fullName}</div>
                <div class="small text-muted">${user.email || 'No Email'}</div>
                <small class="text-muted" style="font-size: 10px;">ID: #${user.id}</small>
            </td>
            <td class="px-4 text-secondary">
                ${user.userRole ? user.userRole.role : 'User'}
            </td>
            <td class="px-4">${statusBadge}</td>
            <td class="px-4 text-end">
                <div class="d-flex justify-content-end gap-2">
                    ${actionBtn}
                </div>
            </td>
        </tr>`;
    });

    if (window.lucide) lucide.createIcons();
}

// User Search Logic
const searchInput = document.getElementById('userSearch');
if (searchInput) {
    searchInput.addEventListener('input', (e) => {
        const searchTerm = e.target.value.toLowerCase();
        if (!window.allUsers) return;

        const filteredUsers = window.allUsers.filter(user => {
            const firstName = (user.firstName || "").toLowerCase();
            const lastName = (user.lastName || "").toLowerCase();
            const email = (user.email || "").toLowerCase();
            const id = user.id ? user.id.toString() : "";

            return firstName.includes(searchTerm) ||
                lastName.includes(searchTerm) ||
                email.includes(searchTerm) ||
                id.includes(searchTerm);
        });

        renderUserTable(filteredUsers);
    });
}

async function toggleUserStatus(userId, status) {
    const action = status === 2 ? "Unblock" : "Block";
    if (!confirm(`Are you sure you want to ${action} this user?`)) return;

    try {
        const response = await fetch(`${API_BASE_URL_ADMIN}/users/status/${userId}?active=${status}`, {
            method: 'POST',
            headers: getAdminHeaders() // <-- ADDED HEADER
        });

        if (response.ok) {
            alert(`User ${action}ed successfully!`);
            loadUserTable();
        } else {
            alert("Error updating user status.");
        }
    } catch (error) {
        console.error("Status Update Error:", error);
    }
}

/**
 * ==========================================
 * PRODUCT & CATEGORY MANAGEMENT SECTION
 * ==========================================
 */
async function loadProductTable() {
    const tableBody = document.getElementById('productTableBody');
    if (!tableBody) return;

    try {
        const response = await fetch(`${API_BASE_URL_ADMIN}/products`, {
            headers: getAdminHeaders() // <-- ADDED HEADER
        });
        const products = await response.json();

        window.allProducts = products;
        renderProductTable(products);
        updateProductStats(products);
    } catch (error) {
        console.error("Failed to load products:", error);
        tableBody.innerHTML = '<tr><td colspan="5" class="text-center text-danger py-4">Failed to load inventory data.</td></tr>';
    }
}

function renderProductTable(products) {
    const tableBody = document.getElementById('productTableBody');
    if (!tableBody) return;

    tableBody.innerHTML = '';

    if (!products || products.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="5" class="text-center text-muted py-4">No products found in inventory.</td></tr>';
        return;
    }

    products.forEach(product => {
        const priceFormatted = parseFloat(product.price || 0).toLocaleString('en-LK', {style: 'currency', currency: 'LKR'});
        const categoryName = product.category ? product.category.name : 'Uncategorized';

        const stockDisplay = product.stockQuantity < 10
            ? `<span class="text-danger fw-bold"><i data-lucide="alert-circle" style="width: 14px; margin-top:-2px;"></i> ${product.stockQuantity}</span>`
            : `<span class="text-success fw-medium">${product.stockQuantity}</span>`;

        tableBody.innerHTML += `
            <tr class="animate-in">
                <td class="px-4 fw-bold text-dark">${product.name}</td>
                <td class="px-4"><span class="badge bg-light text-dark border">${categoryName}</span></td>
                <td class="px-4 text-primary fw-medium">${priceFormatted}</td>
                <td class="px-4">${stockDisplay}</td>
            </tr>`;
    });

    if (window.lucide) lucide.createIcons();
}

function updateProductStats(products) {
    const totalCountEl = document.getElementById('totalProductsCount');
    const lowStockCountEl = document.getElementById('lowStockCount');

    if (totalCountEl && lowStockCountEl) {
        totalCountEl.innerText = products.length;
        const lowStockItems = products.filter(p => p.stockQuantity < 10).length;
        lowStockCountEl.innerText = lowStockItems;
    }
}

const productSearchInput = document.getElementById('productSearch');
if (productSearchInput) {
    productSearchInput.addEventListener('input', (e) => {
        const searchTerm = e.target.value.toLowerCase();
        if (!window.allProducts) return;

        const filteredProducts = window.allProducts.filter(product => {
            const name = (product.name || "").toLowerCase();
            const cat = (product.category && product.category.name ? product.category.name : "").toLowerCase();
            return name.includes(searchTerm) || cat.includes(searchTerm);
        });

        renderProductTable(filteredProducts);
    });
}

async function loadCategories() {
    const listEl = document.getElementById('categoryList');
    if(!listEl) return;

    listEl.innerHTML = '<li class="list-group-item text-center text-muted">Loading...</li>';

    try {
        const response = await fetch(`${API_BASE_URL_ADMIN}/categories`, {
            headers: getAdminHeaders() // <-- ADDED HEADER
        });
        const categories = await response.json();

        listEl.innerHTML = '';
        if (categories.length === 0) {
            listEl.innerHTML = '<li class="list-group-item text-muted">No categories found.</li>';
            return;
        }

        categories.forEach(cat => {
            listEl.innerHTML += `
                <li class="list-group-item d-flex justify-content-between align-items-center">
                    ${cat.name}
                    <span class="badge bg-secondary rounded-pill">ID: ${cat.id}</span>
                </li>`;
        });
    } catch (error) {
        listEl.innerHTML = '<li class="list-group-item text-danger">Error loading categories.</li>';
    }
}

async function saveNewCategory() {
    const nameInput = document.getElementById('newCatName');
    const name = nameInput.value.trim();

    if (!name) {
        alert("Please enter a category name.");
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL_ADMIN}/save-categories`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'User-Role': localStorage.getItem('userRole') // <-- ADDED HEADER
            },
            body: JSON.stringify({name: name})
        });

        if (response.ok) {
            alert("Category Added Successfully!");
            nameInput.value = '';
            loadCategories();
        } else {
            alert("Failed to save category.");
        }
    } catch (error) {
        console.error("Error saving category:", error);
    }
}

/**
 * ==========================================
 * ORDER MANAGEMENT SECTION
 * ==========================================
 */
async function loadOrderTable() {
    const tableBody = document.getElementById('orderTableBody');
    if (!tableBody) return;

    try {
        const response = await fetch(`${API_BASE_URL_ADMIN}/orders`, {
            headers: getAdminHeaders() // <-- ADDED HEADER
        });
        const orderItems = await response.json();

        const groupedOrders = orderItems.reduce((acc, item) => {
            // Ensure data structures exist to prevent null reference errors
            if(!item.order || !item.product) return acc;

            const orderId = item.order.id;
            if (!acc[orderId]) {
                acc[orderId] = {
                    ...item.order,
                    products: []
                };
            }
            acc[orderId].products.push({
                name: item.product.name,
                price: item.priceAtPurchase || 0,
                qty: item.quantity || 1,
                image: item.product.imageUrl || ''
            });
            return acc;
        }, {});

        window.allOrders = Object.values(groupedOrders);
        renderOrderTable(window.allOrders);

        const totalOrdersEl = document.getElementById('totalOrderCount');
        if(totalOrdersEl) totalOrdersEl.innerText = window.allOrders.length;

    } catch (error) {
        console.error("Order Load Error:", error);
        tableBody.innerHTML = '<tr><td colspan="5" class="text-center text-danger">Failed to load orders.</td></tr>';
    }
}

function renderOrderTable(orders) {
    const tableBody = document.getElementById('orderTableBody');
    if (!tableBody) return;

    tableBody.innerHTML = '';

    orders.forEach(order => {
        const userFirstName = order.user ? order.user.firstName : 'Unknown';
        const userLastName = order.user ? order.user.lastName : 'User';
        const userEmail = order.user ? order.user.email : 'No Email';
        const totalAmount = order.totalAmount || 0;
        const orderStatus = order.orderStatus ? order.orderStatus.status : 'Pending';

        tableBody.innerHTML += `
            <tr>
                <td class="px-4 fw-bold text-primary">#ORD-${order.id}</td>
                <td class="px-4">
                    <div class="fw-bold text-dark">${userFirstName} ${userLastName}</div>
                    <div class="small text-muted">${userEmail}</div>
                </td>
                <td class="px-4 fw-bold text-dark">LKR ${totalAmount.toLocaleString()}</td>
                <td class="px-4">
                    <span class="badge bg-info-subtle text-info px-3 py-2 rounded-pill">${orderStatus}</span>
                </td>
                <td class="px-4 text-end">
                    <button class="btn btn-sm btn-outline-primary rounded-pill px-3" onclick="viewOrderDetails(${order.id})">
                        View Details
                    </button>
                </td>
            </tr>`;
    });
}

function viewOrderDetails(orderId) {
    const order = window.allOrders.find(o => o.id === orderId);
    if (!order) return;

    const shipName = order.orderShipping ? order.orderShipping.addressName : 'N/A';
    const shipAddress = order.orderShipping ? order.orderShipping.formattedAddress : 'No Address Provided';
    const totalAmount = order.totalAmount || 0;

    document.getElementById('detailOrderId').innerText = order.id;
    document.getElementById('shipName').innerText = shipName;
    document.getElementById('shipAddress').innerText = shipAddress;
    document.getElementById('detailTotal').innerText = `LKR ${totalAmount.toLocaleString()}`;

    const itemsList = document.getElementById('orderItemsList');
    itemsList.innerHTML = '';
    const URL = "http://localhost:8080/AndroidProjectAPI/uploads/products/";

    order.products.forEach(item => {
        itemsList.innerHTML += `
            <li class="list-group-item d-flex justify-content-between align-items-center py-3">
                <div class="d-flex align-items-center">
                    <img src="${URL + item.image}" class="rounded me-3" style="width: 40px; height: 40px; object-fit: cover;" alt="${item.name}">
                    <div>
                        <div class="fw-bold">${item.name}</div>
                        <small class="text-muted">LKR ${item.price.toLocaleString()} x ${item.qty}</small>
                    </div>
                </div>
                <div class="fw-bold text-dark">LKR ${(item.price * item.qty).toLocaleString()}</div>
            </li>`;
    });

    if (typeof bootstrap !== 'undefined') {
        new bootstrap.Modal(document.getElementById('orderDetailModal')).show();
    }
}
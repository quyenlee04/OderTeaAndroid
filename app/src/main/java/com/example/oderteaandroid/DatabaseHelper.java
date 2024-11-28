package com.example.oderteaandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String databaseName = "TeaOder.db";
    public DatabaseHelper(@Nullable Context context) {
        super(context, "TeaOder.db", null, 5);
    }
    @Override
    public void onCreate(SQLiteDatabase MyDatabase) {
        MyDatabase.execSQL("CREATE TABLE users (" +
                "email TEXT PRIMARY KEY, " +
                "password TEXT NOT NULL, " +
                "avatar TEXT, " +
                "phone TEXT, " +
                "address TEXT)");

        // Create products table
        MyDatabase.execSQL("CREATE TABLE products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "image TEXT NOT NULL, " +
                "description TEXT, " +
                "price REAL NOT NULL, " +
                "quantity INTEGER NOT NULL)");

        // Create cart table
        MyDatabase.execSQL("CREATE TABLE cart (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "product_id INTEGER NOT NULL, " +
                "quantity INTEGER NOT NULL, " +
                "FOREIGN KEY(product_id) REFERENCES products(id))");
    }


    public ArrayList<CartItem> getCartItems(Context context) {
        ArrayList<CartItem> cartItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT p.id, p.name, p.price, c.quantity, p.image " +
                        "FROM cart c JOIN products p ON c.product_id = p.id", null);

        if (cursor.moveToFirst()) {
            do {
                CartItem item = new CartItem(
                        cursor.getInt(0),    // id
                        cursor.getString(1),  // name
                        cursor.getDouble(2),  // price
                        cursor.getInt(3),     // quantity
                        context.getResources().getIdentifier(cursor.getString(4), "drawable", context.getPackageName()) // imageResource
                );
                cartItems.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return cartItems;
    }

    // Thêm sản phẩm vào giỏ hàng
    public boolean addToCart(int productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Get product's available quantity
        Cursor cursor = db.query("products", new String[]{"quantity"}, "id = ?",
                new String[]{String.valueOf(productId)}, null, null, null);

        if (cursor.moveToFirst()) {
            int availableQuantity = cursor.getInt(0);
            cursor.close();

            // Check if requested quantity is available
            if (quantity > availableQuantity) {
                return false;
            }

            // Check if product already exists in cart
            Cursor cartCursor = db.query("cart", new String[]{"quantity"}, "product_id = ?",
                    new String[]{String.valueOf(productId)}, null, null, null);

            if (cartCursor.moveToFirst()) {
                // Update existing cart item
                int currentQuantity = cartCursor.getInt(0);
                cartCursor.close();

                if ((currentQuantity + quantity) > availableQuantity) {
                    return false;
                }

                ContentValues values = new ContentValues();
                values.put("quantity", currentQuantity + quantity);
                return db.update("cart", values, "product_id = ?",
                        new String[]{String.valueOf(productId)}) > 0;
            } else {
                // Add new cart item
                ContentValues values = new ContentValues();
                values.put("product_id", productId);
                values.put("quantity", quantity);
                return db.insert("cart", null, values) != -1;
            }
        }
        return false;
    }
    public boolean updateCartItemQuantity(int productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check product's available quantity
        Cursor cursor = db.query("products", new String[]{"quantity"}, "id = ?",
                new String[]{String.valueOf(productId)}, null, null, null);

        if (cursor.moveToFirst()) {
            int availableQuantity = cursor.getInt(0);
            cursor.close();

            if (quantity > availableQuantity) {
                return false;
            }

            ContentValues values = new ContentValues();
            values.put("quantity", quantity);
            return db.update("cart", values, "product_id = ?",
                    new String[]{String.valueOf(productId)}) > 0;
        }
        return false;
    }


    // Xóa sản phẩm khỏi giỏ hàng
    public boolean removeFromCart(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("cart", "product_id = ?",
                new String[]{String.valueOf(productId)});
        return result > 0;
    }

    // Kiểm tra sản phẩm đã có trong giỏ hàng chưa
    public boolean isProductInCart(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("cart", null, "product_id = ?",
                new String[]{String.valueOf(productId)}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Xóa toàn bộ giỏ hàng
    public void clearCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("cart", null, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1) {
        MyDB.execSQL("drop Table if exists users");
        MyDB.execSQL("drop Table if exists products");
        MyDB.execSQL("drop Table if exists cart");
        onCreate(MyDB);
    }

    public void updateProductQuantities(List<CartItem> cartItems) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            for (CartItem item : cartItems) {
                // Get current product quantity
                Cursor cursor = db.query("products",
                        new String[]{"quantity"},
                        "id = ?",
                        new String[]{String.valueOf(item.getId())},
                        null, null, null);

                if (cursor.moveToFirst()) {
                    int currentQuantity = cursor.getInt(0);
                    int newQuantity = currentQuantity - item.getQuantity();

                    ContentValues values = new ContentValues();
                    values.put("quantity", newQuantity);

                    db.update("products", values, "id = ?",
                            new String[]{String.valueOf(item.getId())});
                }
                cursor.close();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }


    public Boolean insertData(String email, String password) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("password", password);
        contentValues.put("phone", "");  // Initialize with empty values
        contentValues.put("address", "");

        long result = MyDatabase.insert("users", null, contentValues);
        return result != -1;
    }
    public Boolean checkEmail(String email){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("Select * from users where email = ?", new String[]{email});
        if(cursor.getCount() > 0) {
            return true;
        }else {
            return false;
        }
    }
    public Boolean checkEmailPassword(String email, String password){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("Select * from users where email = ? and password = ?", new String[]{email, password});
        if (cursor.getCount() > 0) {
            return true;
        }else {
            return false;
        }
    }



    public void insertSampleProducts() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Clear existing products
        db.delete("products", null, null);

        // Sample product data
        String[][] products = {
                {"Chè thái", "che_thai", "Chè Thái là một món chè nổi tiếng có nguồn gốc từ Thái Lan, được ưa chuộng vì hương vị độc đáo và vẻ ngoài hấp dẫn. Thành phần chính của chè Thái thường bao gồm nước cốt dừa béo ngậy, sầu riêng thơm lừng và thạch rau câu nhiều màu sắc. Ngoài ra, chè Thái có thể kết hợp với các loại trái cây tươi như mít, dừa sợi, đậu xanh, hạt đác và các loại thạch khác để tạo nên hương vị phong phú và đa dạng.Chè Thái có vị ngọt nhẹ, thơm béo, phù hợp cho những ngày nắng nóng vì vị mát lạnh và dễ ăn. Cách chế biến chè Thái cũng khá đơn giản: người làm thường nấu nước cốt dừa với đường, sau đó cho các loại thạch và trái cây vào, ăn kèm với đá lạnh. Đây là một món tráng miệng phổ biến trong các quán chè tại Việt Nam và được nhiều người yêu thích.", "3.99", "5"},
                {"Chè đậu xanh nha đam", "che_dau_xanh_nha_dam", "Chè đậu xanh nha đam là một món chè thanh mát và bổ dưỡng, rất phù hợp để giải nhiệt trong những ngày nắng nóng. Món chè này kết hợp hai nguyên liệu chính là đậu xanh và nha đam, mang lại vị ngọt dịu từ đậu xanh và cảm giác mát lành, giòn giòn từ nha đam. Không chỉ ngon miệng mà còn có nhiều lợi ích cho sức khỏe. Đậu xanh giàu chất xơ và các vitamin, tốt cho hệ tiêu hóa, trong khi nha đam giúp làm mát cơ thể, làm đẹp da và hỗ trợ giải độc. Đây là món ăn dễ làm, thường được dùng lạnh để tăng thêm độ mát lành và tươi ngon.", "4.99", "10"},
                {"Chè bắp nước cốt dừa", "che_bap_nuoc_cot_dua", "Chè bắp nước cốt dừa là một món chè truyền thống của Việt Nam, nổi bật với hương vị ngọt dịu từ bắp (ngô) và độ béo ngậy của nước cốt dừa. Đây là món chè phổ biến, thường được dùng trong các dịp hè để giải nhiệt. Có vị ngọt thơm từ bắp, béo ngậy từ nước cốt dừa, mang đến cảm giác mềm mịn và bùi bùi trong miệng. Món chè này có thể ăn nóng hoặc thêm đá lạnh tùy thích, phù hợp để thưởng thức trong mọi mùa.", "5.99", "30"},
                {"Chè đỗ đen", "che_do_den", "Chè đỗ đen có thể ăn nóng hoặc lạnh, khi ăn lạnh thường được thêm đá và nước cốt dừa để tăng thêm hương vị béo ngậy. Hương vị đặc trưng của chè đỗ đen là ngọt bùi của đỗ, kết hợp với độ béo của nước cốt dừa và mùi thơm nhẹ nhàng. Đây là món chè rất phổ biến trong những ngày hè, giúp làm mát và bổ sung năng lượng cho cơ thể.", "6.99", "25"},
                {"Chè sen long nhãn", "che_sen_long_nhan", "Chè sen long nhãn là một món chè thanh tao, bổ dưỡng, mang hương vị truyền thống. Món chè này gồm hạt sen và long nhãn - hai nguyên liệu có vị ngọt thanh, tính mát, rất thích hợp để giải nhiệt. Hạt sen bùi bùi được ninh mềm, vừa chín tới, giữ được độ bở nhẹ nhưng vẫn dẻo và thơm. Long nhãn được lồng vào hạt sen, tạo nên vẻ ngoài đẹp mắt và hòa quyện giữa vị ngọt thanh của nhãn và vị bùi của hạt sen.", "5.49", "35"},
                {"Chè khúc bạch", "che_khuc_bach", "Chè khúc bạch là một món chè hiện đại, với hương vị thanh mát và màu sắc hấp dẫn. Món chè này gồm các viên \"khúc bạch\" - thường là thạch làm từ gelatin hoặc bột rau câu kết hợp với sữa và kem tươi để tạo nên độ mềm mịn, béo ngậy. Thạch khúc bạch có thể được pha thêm các hương liệu như trà xanh, lá dứa, hoặc hạnh nhân để tạo ra màu sắc đa dạng và hương vị phong phú. Phần nước chè được nấu từ nước đường phèn dịu ngọt và thơm nhẹ, thường kết hợp cùng vải thiều hoặc nhãn tươi để tạo vị ngọt tự nhiên. Chè khúc bạch thường được ăn lạnh, có thêm các loại topping như hạnh nhân rang giòn hoặc các loại trái cây tươi, tạo nên sự cân bằng giữa độ béo của khúc bạch và vị ngọt mát của trái cây. Món chè này rất thích hợp để giải nhiệt và thưởng thức vào những ngày nắng nóng.", "5.49", "35"},
                {"Chè chuối", "che_chuoi", "Chè chuối là một món chè ngọt ngào và phổ biến trong ẩm thực Việt Nam, đặc biệt vào những ngày mưa hay dịp lễ Tết. Món chè này được chế biến từ chuối chín (thường là chuối sứ hoặc chuối tiêu), thái lát hoặc cắt nhỏ, nấu mềm trong nước đường. Chuối trong chè thường có vị ngọt tự nhiên, mềm mịn và thơm. Ngoài chuối, chè chuối còn có các thành phần như bột báng hoặc trân châu, tạo độ mềm dẻo, và nước cốt dừa béo ngậy. Nước chè thường được nấu từ đường phèn, có thể thêm một ít muối để cân bằng vị ngọt, tạo ra sự hài hòa tuyệt vời. Món chè này cũng có thể được ăn kèm với dừa nạo hoặc lạc rang giòn, giúp tăng thêm hương vị và độ giòn cho món ăn. Chè chuối được phục vụ nóng hoặc lạnh, tùy theo sở thích của mỗi người. Món chè này có vị béo ngậy của dừa, độ ngọt tự nhiên của chuối và độ mềm dẻo của bột báng, tạo nên một món ăn vừa dễ làm lại thơm ngon, thích hợp cho nhiều dịp thưởng thức.", "5.49", "35"},
                {"Chè thập cẩm", "che_thap_cam", "Chè thập cẩm là một món chè truyền thống phổ biến, được yêu thích nhờ vào sự kết hợp đa dạng của nhiều nguyên liệu, tạo nên một hương vị phong phú và hấp dẫn. Món chè này thường bao gồm các thành phần như đậu xanh, đậu đỏ, đậu đen, bột báng, trân châu, khoai lang, khoai môn, và thạch rau câu, tất cả được nấu chung trong một nồi nước đường ngọt thanh. Mỗi nguyên liệu trong chè thập cẩm đều có hương vị riêng, từ sự bùi bùi của các loại đậu, độ mềm dẻo của bột báng, cho đến độ giòn của thạch rau câu và khoai lang, khoai môn. Để tăng thêm sự béo ngậy, chè thập cẩm thường được kết hợp với nước cốt dừa, tạo nên một hương vị ngọt ngào, thanh mát nhưng vẫn đầy đủ độ béo. Chè thập cẩm có thể được thưởng thức nóng hoặc lạnh, tùy theo sở thích và mùa. Món chè này không chỉ ngon mà còn bổ dưỡng, thích hợp cho cả gia đình vào những dịp sum họp hoặc trong các bữa tiệc. Sự đa dạng trong nguyên liệu và hương vị khiến chè thập cẩm trở thành món ăn rất được yêu thích.", "5.49", "35"},
                {"Chè bơ", "che_bo", "Chè bơ là một món chè độc đáo và thơm ngon, nổi bật với vị béo ngậy của bơ tươi và thường được kết hợp với sữa hoặc nước cốt dừa để tăng độ sánh mịn. Chè bơ có màu xanh nhạt tự nhiên từ bơ, tạo nên sự bắt mắt và hấp dẫn. Thạch bơ là thành phần chính, có kết cấu mềm mịn, tan ngay khi thưởng thức, để lại vị thơm ngậy đặc trưng. Món chè này thường được trang trí thêm bằng nước cốt dừa, dừa khô hoặc một chút đậu phộng rang giòn để tăng hương vị và độ phong phú. Chè bơ thường được ướp lạnh, mang lại cảm giác tươi mát và sảng khoái, rất phù hợp cho những ai yêu thích hương vị ngọt béo tự nhiên.", "5.49", "35"},
                {"Chè nấm tuyết", "che_nam_tuyet", "Chè nấm tuyết là một món chè thanh mát và bổ dưỡng, nổi bật với thành phần chính là nấm tuyết (ngân nhĩ), mang lại độ giòn sần sật đặc trưng. Nấm tuyết có màu trắng trong, sau khi nấu chín sẽ có kết cấu mềm dai và hương vị nhẹ nhàng. Món chè này thường được nấu cùng với các nguyên liệu tốt cho sức khỏe như táo đỏ, hạt sen, long nhãn và kỷ tử. Khi kết hợp, chè có hương vị ngọt dịu nhờ đường phèn, tạo cảm giác thanh mát và dễ chịu.", "5.49", "35"},
                {"Chè bưởi", "che_buoi", "Chè bưởi là một món chè đặc trưng của ẩm thực miền Nam Việt Nam, nổi bật với hương vị thanh mát, ngọt dịu và chút chua nhẹ đặc trưng từ bưởi. Món chè này có thành phần chính là cùi bưởi tươi, được tách ra thành từng sợi nhỏ, dẻo, có vị hơi đắng, nhưng khi kết hợp với nước đường phèn ngọt thanh thì lại tạo nên sự hòa quyện rất thú vị.", "5.49", "35"},
        };

        for (String[] product : products) {
            ContentValues values = new ContentValues();
            values.put("name", product[0]);
            values.put("image", product[1]);
            values.put("description", product[2]);
            values.put("price", Double.parseDouble(product[3]));
            values.put("quantity", Integer.parseInt(product[4]));
            db.insert("products", null, values);
        }
    }
    public void ensureUserExists(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("users", null, "email = ?",
                new String[]{email}, null, null, null);

        if (!cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put("email", email);
            values.put("password", "default_password"); // Adding required password field
            values.put("phone", "");
            values.put("address", "");
            values.put("avatar", "");
            db.insert("users", null, values);
        }
        cursor.close();
    }

    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM products", null);

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getDouble(4),
                        cursor.getInt(5)
                );
                products.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return products;
    }

    public Product getProductById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("products", null, "id=?",
                new String[]{String.valueOf(id)}, null, null, null);

        Product product = null;
        if (cursor.moveToFirst()) {
            product = new Product(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getDouble(4),
                    cursor.getInt(5)
            );
        }
        cursor.close();
        return product;
    }



    // Update the updateUserProfile method
    public boolean updateUserProfile(String email, String phone, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone", phone);
        values.put("address", address);

        int result = db.update("users", values, "email = ?", new String[]{email});
        return result > 0;
    }


    public UserData getUserData(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"email", "phone", "address", "avatar"};
        Cursor cursor = db.query("users", columns, "email = ?",
                new String[]{email}, null, null, null);

        UserData userData = null;
        if (cursor != null && cursor.moveToFirst()) {
            userData = new UserData(
                    cursor.getString(0),  // email is column 0
                    cursor.getString(1),  // phone is column 1
                    cursor.getString(2),  // address is column 2
                    cursor.getString(3)   // avatar is column 3
            );
            cursor.close();
        }
        return userData;
    }



}
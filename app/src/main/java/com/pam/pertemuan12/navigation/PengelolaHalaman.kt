package com.pam.pertemuan12.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pam.pertemuan12.view.Pengembalian.DestinasiPengembalianDetail
import com.pam.pertemuan12.view.Pengembalian.DestinasiPengembalianHome
import com.pam.pertemuan12.view.Pengembalian.DestinasiPengembalianInsert
import com.pam.pertemuan12.view.Pengembalian.DestinasiPengembalianUpdate
import com.pam.pertemuan12.view.Pengembalian.DetailPengembalianView
import com.pam.pertemuan12.view.Pengembalian.HomePengembalianScreen
import com.pam.pertemuan12.view.Pengembalian.InsertPengembalianScreen
import com.pam.pertemuan12.view.Pengembalian.UpdatePengembalianView
import com.pam.pertemuan12.view.anggota.DestinasiAnggotaDetail
import com.pam.pertemuan12.view.anggota.DestinasiAnggotaHome
import com.pam.pertemuan12.view.anggota.DestinasiAnggotaInsert
import com.pam.pertemuan12.view.anggota.DestinasiAnggotaUpdate
import com.pam.pertemuan12.view.anggota.DetailAnggotaView
import com.pam.pertemuan12.view.anggota.HomeAnggotaScreen
import com.pam.pertemuan12.view.anggota.InsertAnggotaScreen
import com.pam.pertemuan12.view.anggota.UpdateAnggotaView
import com.pam.pertemuan12.view.buku.DestinasiBukuDetail
import com.pam.pertemuan12.view.buku.DestinasiBukuHome
import com.pam.pertemuan12.view.buku.DestinasiBukuInsert
import com.pam.pertemuan12.view.buku.DestinasiBukuUpdate
import com.pam.pertemuan12.view.buku.DetailBukuView
import com.pam.pertemuan12.view.buku.EntryMhsScreen
import com.pam.pertemuan12.view.buku.HomeScreen
import com.pam.pertemuan12.view.buku.UpdateView
import com.pam.pertemuan12.view.home.DestinasiSplash
import com.pam.pertemuan12.view.home.HomeView
import com.pam.pertemuan12.view.peminjaman.DestinasiPeminjamanDetail
import com.pam.pertemuan12.view.peminjaman.DestinasiPeminjamanHome
import com.pam.pertemuan12.view.peminjaman.DestinasiPeminjamanInsert
import com.pam.pertemuan12.view.peminjaman.DestinasiPeminjamanUpdate
import com.pam.pertemuan12.view.peminjaman.DetailPeminjamanView
import com.pam.pertemuan12.view.peminjaman.HomePeminjamanScreen
import com.pam.pertemuan12.view.peminjaman.InsertPeminjamanScreen
import com.pam.pertemuan12.view.peminjaman.UpdatePeminjamanView

@Composable
fun PengelolaHalaman(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = DestinasiSplash.route,
        modifier = Modifier
    ) {
        // Home Splash
        composable(DestinasiSplash.route) {
            HomeView(
                onHomeClick = { },
                onAnggotaClick = {
                    navController.navigate(DestinasiAnggotaHome.route)
                },
                onBukuClick = {
                    navController.navigate(DestinasiBukuHome.route)
                },
                onPeminjamanClick = {
                    navController.navigate(DestinasiPeminjamanHome.route)
                },
                onPengembalianClick = {
                    navController.navigate(DestinasiPengembalianHome.route)
                }
            )
        }

        // Home Pengembalian Screen
        composable(DestinasiPengembalianHome.route) {
            HomePengembalianScreen(
                navigateToltemEntry = { navController.navigate(DestinasiPengembalianInsert.route) },
                onDetailClick = { id_pengembalian ->
                    navController.navigate("${DestinasiPengembalianDetail.route}/$id_pengembalian")
                },
                onHomeClick = { navController.navigate(DestinasiSplash.route) },
                onAnggotaClick = { navController.navigate(DestinasiAnggotaHome.route) },
                onBukuClick = { navController.navigate(DestinasiBukuHome.route) },
                onPeminjamanClick = { navController.navigate(DestinasiPeminjamanHome.route) },
                onPengembalianClick = { },
                navigateBack = { navController.popBackStack() },
            )
        }

        // Insert Pengembalian Screen
        composable(DestinasiPengembalianInsert.route) {
            InsertPengembalianScreen(navigateBack = {
                navController.navigate(DestinasiPengembalianHome.route) {
                    popUpTo(DestinasiPengembalianHome.route) {
                        inclusive = true
                    }
                }
            })
        }

        // Detail Pengembalian Screen
        composable(
            route = "${DestinasiPengembalianDetail.route}/{id_pengembalian}",
            arguments = listOf(navArgument("id_pengembalian") { type = NavType.StringType })
        ) { backStackEntry ->
            val id_pengembalian = backStackEntry.arguments?.getString("id_pengembalian") ?: ""
            DetailPengembalianView(
                id_pengembalian = id_pengembalian,
                navigateBack = {
                    navController.navigate(DestinasiPengembalianHome.route) {
                        popUpTo(DestinasiPengembalianHome.route) {
                            inclusive = true
                        }
                    }
                },
                onHomeClick = { navController.navigate(DestinasiSplash.route) },
                onAnggotaClick = { navController.navigate(DestinasiAnggotaHome.route) },
                onBukuClick = { navController.navigate(DestinasiBukuHome.route) },
                onPeminjamanClick = { navController.navigate(DestinasiPeminjamanHome.route) },
                onPengembalianClick = { },
                onClick = {
                    navController.navigate("${DestinasiPengembalianUpdate.route}/$id_pengembalian")
                }
            )
        }

        // Update Pengembalian Screen
        composable(
            route = "${DestinasiPengembalianUpdate.route}/{id_pengembalian}",
            arguments = listOf(navArgument("id_pengembalian") { type = NavType.StringType })
        ) { backStackEntry ->
            val id_pengembalian = backStackEntry.arguments?.getString("id_pengembalian") ?: ""
            UpdatePengembalianView(
                id_pengembalian = id_pengembalian,
                navigateBack = {
                    navController.navigate(DestinasiPengembalianHome.route) {
                        popUpTo(DestinasiPengembalianHome.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // Home Peminjaman Screen
        composable(DestinasiPeminjamanHome.route) {
            HomePeminjamanScreen(
                navigateToltemEntry = { navController.navigate(DestinasiPeminjamanInsert.route) },
                onDetailClick = { id_peminjaman ->
                    navController.navigate("${DestinasiPeminjamanDetail.route}/$id_peminjaman")
                },
                onHomeClick = { navController.navigate(DestinasiSplash.route) },
                onAnggotaClick = { navController.navigate(DestinasiAnggotaHome.route) },
                onBukuClick = { navController.navigate(DestinasiBukuHome.route) },
                onPeminjamanClick = { },
                onPengembalianClick = { navController.navigate(DestinasiPengembalianHome.route) },
                navigateBack = { navController.popBackStack() },
            )
        }

        // Insert Peminjaman Screen
        composable(DestinasiPeminjamanInsert.route) {
            InsertPeminjamanScreen(navigateBack = {
                navController.navigate(DestinasiPeminjamanHome.route) {
                    popUpTo(DestinasiPeminjamanHome.route) {
                        inclusive = true
                    }
                }
            })
        }

        // Detail Peminjaman Screen
        composable(
            route = "${DestinasiPeminjamanDetail.route}/{id_peminjaman}",
            arguments = listOf(navArgument("id_peminjaman") { type = NavType.StringType })
        ) { backStackEntry ->
            val id_peminjaman = backStackEntry.arguments?.getString("id_peminjaman") ?: ""
            DetailPeminjamanView(
                id_peminjaman = id_peminjaman,
                navigateBack = {
                    navController.navigate(DestinasiPeminjamanHome.route) {
                        popUpTo(DestinasiPeminjamanHome.route) {
                            inclusive = true
                        }
                    }
                },
                onHomeClick = { navController.navigate(DestinasiSplash.route) },
                onAnggotaClick = { navController.navigate(DestinasiAnggotaHome.route) },
                onBukuClick = { navController.navigate(DestinasiBukuHome.route) },
                onPeminjamanClick = { },
                onPengembalianClick = { navController.navigate(DestinasiPengembalianHome.route) },
                onClick = {
                    navController.navigate("${DestinasiPeminjamanUpdate.route}/$id_peminjaman")
                }
            )
        }

        // Update Peminjaman Screen
        composable(
            route = "${DestinasiPeminjamanUpdate.route}/{id_peminjaman}",
            arguments = listOf(navArgument("id_peminjaman") { type = NavType.StringType })
        ) { backStackEntry ->
            val id_peminjaman = backStackEntry.arguments?.getString("id_peminjaman") ?: ""
            UpdatePeminjamanView(
                id_peminjaman = id_peminjaman,
                navigateBack = {
                    navController.navigate(DestinasiPeminjamanHome.route) {
                        popUpTo(DestinasiPeminjamanHome.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(DestinasiAnggotaHome.route) {
            HomeAnggotaScreen(
                navigateToltemEntry = { navController.navigate(DestinasiAnggotaInsert.route) },
                onDetailClick = { id_anggota ->
                    navController.navigate("${DestinasiAnggotaDetail.route}/$id_anggota")
                },
                onHomeClick = { navController.navigate(DestinasiSplash.route) },
                onAnggotaClick = { },
                onBukuClick = { navController.navigate(DestinasiBukuHome.route) },
                onPeminjamanClick = { navController.navigate(DestinasiPeminjamanHome.route) },
                onPengembalianClick = { navController.navigate(DestinasiPengembalianHome.route) },
                navigateBack = { navController.popBackStack() }
            )
        }

        // Insert Anggota Screen
        composable(DestinasiAnggotaInsert.route) {
            InsertAnggotaScreen(navigateBack = {
                navController.navigate(DestinasiAnggotaHome.route) {
                    popUpTo(DestinasiAnggotaHome.route) {
                        inclusive = true
                    }
                }
            })
        }

        // Detail Anggota Screen
        composable(
            route = "${DestinasiAnggotaDetail.route}/{id_anggota}",
            arguments = listOf(navArgument("id_anggota") { type = NavType.StringType })
        ) { backStackEntry ->
            val id_anggota = backStackEntry.arguments?.getString("id_anggota") ?: ""
            DetailAnggotaView(
                id_anggota = id_anggota,
                navigateBack = {
                    navController.navigate(DestinasiAnggotaHome.route) {
                        popUpTo(DestinasiAnggotaHome.route) {
                            inclusive = true
                        }
                    }
                },
                onClick = {
                    navController.navigate("${DestinasiAnggotaUpdate.route}/$id_anggota")
                },
                onHomeClick = { navController.navigate(DestinasiSplash.route) },
                onAnggotaClick = { },
                onBukuClick = { navController.navigate(DestinasiBukuHome.route) },
                onPeminjamanClick = { navController.navigate(DestinasiPeminjamanHome.route) },
                onPengembalianClick = { navController.navigate(DestinasiPengembalianHome.route) },
            )
        }

        // Update Screen
        composable(
            route = "${DestinasiAnggotaUpdate.route}/{id_anggota}",
            arguments = listOf(navArgument("id_anggota") { type = NavType.StringType })
        ) { backStackEntry ->
            val id_anggota = backStackEntry.arguments?.getString("id_anggota") ?: ""
            UpdateAnggotaView(
                id_anggota = id_anggota,
                navigateBack = {
                    navController.navigate(DestinasiAnggotaHome.route) {
                        popUpTo(DestinasiAnggotaHome.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // Home Screen
        composable(DestinasiBukuHome.route) {
            HomeScreen(
                navigateToltemEntry = { navController.navigate(DestinasiBukuInsert.route) },
                onDetailClick = { id_buku ->
                    navController.navigate("${DestinasiBukuDetail.route}/$id_buku")
                },
                onHomeClick = { navController.navigate(DestinasiSplash.route) },
                onAnggotaClick = { navController.navigate(DestinasiAnggotaHome.route) },
                onBukuClick = { },
                onPeminjamanClick = { navController.navigate(DestinasiPeminjamanHome.route) },
                onPengembalianClick = { navController.navigate(DestinasiPengembalianHome.route) },
                navigateBack = { navController.popBackStack() },
            )
        }

        // Insert Screen
        composable(DestinasiBukuInsert.route) {
            EntryMhsScreen(navigateBack = {
                navController.navigate(DestinasiBukuHome.route) {
                    popUpTo(DestinasiBukuHome.route) {
                        inclusive = true
                    }
                }
            })
        }

        // Detail Screen
        composable(
            route = "${DestinasiBukuDetail.route}/{id_buku}",
            arguments = listOf(navArgument("id_buku") { type = NavType.StringType })
        ) { backStackEntry ->
            val id_buku = backStackEntry.arguments?.getString("id_buku") ?: ""
            DetailBukuView(
                id_buku = id_buku,
                navigateBack = {
                    navController.navigate(DestinasiBukuHome.route) {
                        popUpTo(DestinasiBukuHome.route) {
                            inclusive = true
                        }
                    }
                },
                onHomeClick = { navController.navigate(DestinasiSplash.route) },
                onAnggotaClick = { navController.navigate(DestinasiAnggotaHome.route) },
                onBukuClick = { },
                onPeminjamanClick = { navController.navigate(DestinasiPeminjamanHome.route) },
                onPengembalianClick = { navController.navigate(DestinasiPengembalianHome.route) },
                onClick = {
                    navController.navigate("${DestinasiBukuUpdate.route}/$id_buku")
                }
            )
        }

        // Update Screen
        composable(
            route = "${DestinasiBukuUpdate.route}/{id_buku}",
            arguments = listOf(navArgument("id_buku") { type = NavType.StringType })
        ) { backStackEntry ->
            val id_buku = backStackEntry.arguments?.getString("id_buku") ?: ""
            UpdateView(
                id_buku = id_buku,
                navigateBack = {
                    navController.navigate(DestinasiBukuHome.route) {
                        popUpTo(DestinasiBukuHome.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}
